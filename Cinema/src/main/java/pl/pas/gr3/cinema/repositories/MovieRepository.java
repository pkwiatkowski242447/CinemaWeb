package pl.pas.gr3.cinema.repositories;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import pl.pas.gr3.cinema.exceptions.mapping.MovieDocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.mappers.MovieMapper;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieRepository extends MongoRepository<Movie> {

    public MovieRepository(String databaseName) {
        super.initDBConnection(databaseName);

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(movieCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "movie_title", "movie_base_price", "scr_room_number", "number_of_available_seats"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the movie object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
                                        }
                                        "movie_title": {
                                            "description": "String containing the name / title of the movie.",
                                            "bsonType": "string",
                                            "minLength": 1,
                                            "maxLength": 150
                                        }
                                        "movie_base_price": {
                                            "description": "Double value representing movie base price - before taking ticketType into account.",
                                            "bsonType": "double",
                                            "minimum": 0,
                                            "maximum": 100
                                        }
                                        "scr_room_number": {
                                            "description": "Integer value representing number of the screening room which movie is aired in.",
                                            "bsonType": "int",
                                            "minimum": 1,
                                            "maximum": 30
                                        }
                                        "number_of_available_seats": {
                                            "description": "Integer value representing maximum number of available seats inside screening room.",
                                            "bsonType": "int",
                                            "minimum": 0,
                                            "maximum": 120
                                        }
                                    }
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
        }
    }

    // Create methods

    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieRepositoryException {
        Movie movie;
        try {
            movie = new Movie(UUID.randomUUID(), movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
            MovieDoc movieDoc = MovieMapper.toMovieDoc(movie);
            getMovieCollection().insertOne(movieDoc);
        } catch (MongoException exception) {
            throw new MovieRepositoryCreateException(exception.getMessage(), exception);
        }
        return movie;
    }

    // Read methods

    @Override
    public Movie findByUUID(UUID movieID) throws MovieRepositoryException {
        Movie movie;
        try {
            MovieDoc foundMovieDoc = findMovieDoc(movieID);
            movie = MovieMapper.toMovie(foundMovieDoc);
        } catch (MongoException | MovieDocNullReferenceException exception) {
            throw new MovieRepositoryReadException(exception.getMessage(), exception);
        }
        return movie;
    }

    @Override
    public List<UUID> findAllUUIDs() throws MovieRepositoryException {
        List<UUID> listOfAllUUIDs = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson movieFilter = Filters.empty();
            for (MovieDoc movieDoc : getMovieCollection().find(movieFilter)) {
                listOfAllUUIDs.add(movieDoc.getMovieID());
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new MovieRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllUUIDs;
    }

    @Override
    public List<Movie> findAll() throws MovieRepositoryException {
        List<Movie> listOfAllMovies;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson movieFilter = Filters.empty();
            listOfAllMovies = findMovies(movieFilter);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new MovieRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllMovies;
    }

    public List<Ticket> getListOfTicketsForMovie(UUID movieID) {
        List<Ticket> listOfActiveTickets;
        List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("movie_id", movieID)));
        listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
        return listOfActiveTickets;
    }

    // Update methods

    @Override
    public void update(Movie movie) throws MovieRepositoryException {
        try {
            MovieDoc newMovieDoc = MovieMapper.toMovieDoc(movie);
            Bson movieFilter = Filters.eq("_id", movie.getMovieID());
            MovieDoc updatedMovieDoc = getMovieCollection().findOneAndReplace(movieFilter, newMovieDoc);
            if (updatedMovieDoc == null) {
                throw new MovieDocNullReferenceException("Movie object representation for given movie object could not be found in the database.");
            }
        } catch (MongoException | MovieDocNullReferenceException exception) {
            throw new MovieRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    public void delete(UUID movieID) throws MovieRepositoryException {
        try {
            Bson ticketFilter = Filters.eq("movie_id", movieID);
            List<TicketDoc> listOfTicketDocs = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            if (listOfTicketDocs.isEmpty()) {
                Bson movieFilter = Filters.eq("_id", movieID);
                MovieDoc removedMovieDoc = getMovieCollection().findOneAndDelete(movieFilter);
                if (removedMovieDoc == null) {
                    throw new MovieDocNullReferenceException("Movie object with given ID could not be found in the database.");
                }
            } else {
                throw new ResourceIsCurrentlyUsedDeleteException("Movie object with given ID is currently used in some tickets.");
            }
        } catch (MongoException | MovieDocNullReferenceException | ResourceIsCurrentlyUsedDeleteException exception) {
            throw new MovieRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    private List<Movie> findMovies(Bson movieFilter) {
        List<Movie> listOfFoundMovies = new ArrayList<>();
        for (MovieDoc movieDoc : getMovieCollection().find(movieFilter)) {
            listOfFoundMovies.add(MovieMapper.toMovie(movieDoc));
        }
        return listOfFoundMovies;
    }
}
