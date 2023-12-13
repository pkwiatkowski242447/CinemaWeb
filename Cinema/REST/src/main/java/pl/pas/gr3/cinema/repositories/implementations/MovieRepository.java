package pl.pas.gr3.cinema.repositories.implementations;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.mapping.MovieDocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.movie.*;
import pl.pas.gr3.cinema.exceptions.repositories.other.movie.ResourceIsCurrentlyUsedDeleteException;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.mappers.MovieMapper;
import pl.pas.gr3.cinema.messages.repositories.MongoRepositoryMessages;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.repositories.interfaces.MovieRepositoryInterface;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MovieRepository extends MongoRepository implements MovieRepositoryInterface {

    private final String databaseName;
    private static final Logger logger = LoggerFactory.getLogger(MovieRepository.class);
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
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

    public MovieRepository() {
        this.databaseName = "default";
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(movieCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(movieCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(this.validationOptions);
            mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
        }
    }

    @PostConstruct
    private void initializeDatabaseState() {
        UUID movieNo1ID = UUID.fromString("f3e66584-f793-4f5e-9dec-904ca00e2dd6");
        UUID movieNo2ID = UUID.fromString("9b9e1de2-099b-415d-96b4-f7cfc8897318");
        UUID movieNo3ID = UUID.fromString("b69b4714-e307-4ebf-b491-e3720f963f53");
        Movie movieNo1 = new Movie(movieNo1ID, "Pulp Fiction", 45.75, 1, 100);
        Movie movieNo2 = new Movie(movieNo2ID, "Cars", 30.50, 2, 50);
        Movie movieNo3 = new Movie(movieNo3ID, "Joker", 50.00, 3, 75);

        List<Movie> listOfMovies = List.of(movieNo1, movieNo2, movieNo3);
        for (Movie movie : listOfMovies) {
            Bson filter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, movie.getMovieID());
            if (this.getMovieCollection().find(filter).first() == null) {
                this.getMovieCollection().insertOne(MovieMapper.toMovieDoc(movie));
            }
        }
    }

    @PreDestroy
    private void restoreDatabaseState() {
        try {
            List<UUID> listOfAllUUIDs = this.findAllUUIDs();
            for (UUID movieID : listOfAllUUIDs) {
                this.delete(movieID);
            }
        } catch (MovieRepositoryException exception) {
            logger.debug(exception.getMessage());
        }
        this.close();
    }

    public MovieRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);
        this.getMovieCollection().drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(this.validationOptions);
        mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
    }

    // Create methods

    @Override
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
        } catch (MovieDocNullReferenceException exception) {
            throw new MovieRepositoryMovieNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
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
        List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.MOVIE_IDENTIFIER, movieID)));
        listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
        return listOfActiveTickets;
    }

    // Update methods

    @Override
    public void update(Movie movie) throws MovieRepositoryException {
        try {
            MovieDoc newMovieDoc = MovieMapper.toMovieDoc(movie);
            Bson movieFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, movie.getMovieID());
            MovieDoc updatedMovieDoc = getMovieCollection().findOneAndReplace(movieFilter, newMovieDoc);
            if (updatedMovieDoc == null) {
                throw new MovieDocNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
            }
        } catch (MongoException | MovieDocNullReferenceException exception) {
            throw new MovieRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    @Override
    public void delete(UUID movieID) throws MovieRepositoryException {
        try {
            Bson ticketFilter = Filters.eq(MongoRepositoryConstants.MOVIE_IDENTIFIER, movieID);
            List<TicketDoc> listOfTicketDocs = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            if (listOfTicketDocs.isEmpty()) {
                Bson movieFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, movieID);
                MovieDoc removedMovieDoc = getMovieCollection().findOneAndDelete(movieFilter);
                if (removedMovieDoc == null) {
                    throw new MovieDocNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
                }
            } else {
                throw new ResourceIsCurrentlyUsedDeleteException(MongoRepositoryMessages.MOVIE_HAS_UNFINISHED_ALLOCATIONS);
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
