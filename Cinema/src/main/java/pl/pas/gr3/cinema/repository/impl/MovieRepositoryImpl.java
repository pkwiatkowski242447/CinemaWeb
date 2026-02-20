package pl.pas.gr3.cinema.repository.impl;

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
import pl.pas.gr3.cinema.exception.bad_request.MovieDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.MovieUpdateException;
import pl.pas.gr3.cinema.exception.not_found.MovieNotFoundException;
import pl.pas.gr3.cinema.util.consts.model.MovieConstants;
import pl.pas.gr3.cinema.util.consts.model.TicketConstants;
import pl.pas.gr3.cinema.exception.bad_request.MovieCreateException;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.repository.api.MovieRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MovieRepositoryImpl extends MongoRepository implements MovieRepository {

    private final String databaseName;
    private static final Logger logger = LoggerFactory.getLogger(MovieRepositoryImpl.class);
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

    public MovieRepositoryImpl() {
        this.databaseName = "default";
        super.initDBConnection(databaseName);

        mongoDatabase.getCollection(movieCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(movieCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
        }
    }

    @PostConstruct
    private void initializeDatabaseState() {
        UUID movieIdNo1 = UUID.fromString("f3e66584-f793-4f5e-9dec-904ca00e2dd6");
        UUID movieIdNo2 = UUID.fromString("9b9e1de2-099b-415d-96b4-f7cfc8897318");
        UUID movieIdNo3 = UUID.fromString("b69b4714-e307-4ebf-b491-e3720f963f53");

        Movie movieNo1 = new Movie(movieIdNo1, "Pulp Fiction", 45.75, 1, 100);
        Movie movieNo2 = new Movie(movieIdNo2, "Cars", 30.50, 2, 50);
        Movie movieNo3 = new Movie(movieIdNo3, "Joker", 50.00, 3, 75);

        List<Movie> movies = List.of(movieNo1, movieNo2, movieNo3);
        movies.forEach(movie -> {
            Bson filter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movie.getId());
            if (getMovieCollection().find(filter).first() == null) getMovieCollection().insertOne(movie);
        });
    }

    @PreDestroy
    private void restoreDatabaseState() {
        try {
            List<Movie> listOfAllMovies = findAll();
            listOfAllMovies.forEach(movie -> delete(movie.getId()));
        } catch (Exception exception) {
            logger.debug(exception.getMessage());
        }
        close();
    }

    public MovieRepositoryImpl(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);
        mongoDatabase.getCollection(movieCollectionName).drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
    }

    /* CREATE */

    @Override
    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) {
        try {
            Movie movie = new Movie(UUID.randomUUID(), movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
            getMovieCollection().insertOne(movie);
            return movie;
        } catch (MongoException exception) {
            throw new MovieCreateException(exception);
        }
    }

    /* READ */

    @Override
    public Movie findByUUID(UUID movieId) {
        try {
            return findMovie(movieId);
        } catch (MongoException exception) {
            throw new MovieNotFoundException(exception);
        }
    }

    @Override
    public List<Movie> findAll() {
        List<Movie> listOfAllMovies;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson movieFilter = Filters.empty();
            listOfAllMovies = findMovies(movieFilter);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new MovieNotFoundException(exception);
        }
        return listOfAllMovies;
    }

    public List<Ticket> getListOfTicketsForMovie(UUID movieId) {
        List<Ticket> listOfActiveTickets;
        List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(TicketConstants.MOVIE_ID, movieId)));
        listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
        return listOfActiveTickets;
    }

    /* UPDATE */

    @Override
    public void update(Movie movie) {
        try {
            Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movie.getId());
            Movie updatedMovie = getMovieCollection().findOneAndReplace(movieFilter, movie);
            if (updatedMovie == null) throw new MovieNotFoundException();
        } catch (MongoException exception) {
            throw new MovieUpdateException(exception);
        }
    }

    /* DELETE */

    @Override
    public void delete(UUID movieId) {
        try {
            Bson ticketFilter = Filters.eq(TicketConstants.MOVIE_ID, movieId);
            List<Ticket> listOfTicket = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            if (listOfTicket.isEmpty()) {
                Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movieId);
                Movie removedMovie = getMovieCollection().findOneAndDelete(movieFilter);
                if (removedMovie == null) throw new MovieNotFoundException();
            } else {
                throw new MovieDeleteException();
            }
        } catch (MongoException exception) {
            throw new MovieDeleteException(exception);
        }
    }

    /* OTHER */

    private List<Movie> findMovies(Bson movieFilter) {
        List<Movie> listOfFoundMovies;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            listOfFoundMovies = getMovieCollection().find(movieFilter).into(new ArrayList<>());
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new MovieNotFoundException(exception);
        }
        return listOfFoundMovies;
    }
}