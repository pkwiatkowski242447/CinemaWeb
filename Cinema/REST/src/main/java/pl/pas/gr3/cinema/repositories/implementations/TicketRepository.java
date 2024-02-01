package pl.pas.gr3.cinema.repositories.implementations;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.pas.gr3.cinema.consts.model.MovieConstants;
import pl.pas.gr3.cinema.consts.model.TicketConstants;
import pl.pas.gr3.cinema.consts.model.UserConstants;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.MovieNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.ObjectNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.TicketNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.UserNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.*;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientNotActiveException;
import pl.pas.gr3.cinema.messages.repositories.MongoRepositoryMessages;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.model.users.User;
import pl.pas.gr3.cinema.repositories.interfaces.TicketRepositoryInterface;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class TicketRepository extends MongoRepository implements TicketRepositoryInterface {

    private final String databaseName;
    private final static Logger logger = LoggerFactory.getLogger(TicketRepository.class);
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
            Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "movie_time", "movie_final_price", "user_id", "movie_id"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the ticket object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "movie_time": {
                                            "description": "Variable holding value of the date, when movie will air.",
                                            "bsonType": "date"
                                        }
                                        "ticket_final_price": {
                                            "description": "Double value holding final price of the ticket, that is after applying all discounts.",
                                            "bsonType": "double"
                                        }
                                        "user_id": {
                                            "description": "Id of the user object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "movie_id": {
                                            "description": "Id of the movie object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                    }
                                }
                            }
                            """));

    public TicketRepository() {
        this.databaseName = "default";
        super.initDBConnection(databaseName);

        mongoDatabase.getCollection(ticketCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(ticketCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
        }
    }

    @PostConstruct
    private void initializeDatabaseState() {
        // Client data

        UUID clientIDNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIDNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        UUID clientIDNo3 = UUID.fromString("30392328-2cae-4e76-abb8-b1aa8f58a9e4");

        UUID adminIDNo1 = UUID.fromString("17dad3c7-7605-4808-bec5-d6f46abd23b8");
        UUID adminIDNo2 = UUID.fromString("ca857499-cdd5-4de3-a8d2-1ba7afcec2ef");
        UUID adminIDNo3 = UUID.fromString("07f97385-a2a3-474e-af61-f53d14a64198");

        UUID staffIDNo1 = UUID.fromString("67a85b0f-d063-4c9b-b223-fcc606c00f2f");
        UUID staffIDNo2 = UUID.fromString("3d8ef63c-f99d-445c-85d0-4b14e68fc5a1");
        UUID staffIDNo3 = UUID.fromString("86e394dd-e192-4390-b4e4-76029c879857");

        Client clientNo1 = new Client(clientIDNo1, "NewClientLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Client clientNo2 = new Client(clientIDNo2, "NewClientLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Client clientNo3 = new Client(clientIDNo3, "NewClientLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        Admin adminNo1 = new Admin(adminIDNo1, "NewAdminLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Admin adminNo2 = new Admin(adminIDNo2, "NewAdminLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Admin adminNo3 = new Admin(adminIDNo3, "NewAdminLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        Staff staffNo1 = new Staff(staffIDNo1, "NewStaffLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Staff staffNo2 = new Staff(staffIDNo2, "NewStaffLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Staff staffNo3 = new Staff(staffIDNo3, "NewStaffLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        List<User> listOfClients = List.of(clientNo1, clientNo2, clientNo3, adminNo1, adminNo2, adminNo3, staffNo1, staffNo2, staffNo3);
        for (User user : listOfClients) {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, user.getUserID());
            if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(Client.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(Admin.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(Staff.class)) {
                this.getClientCollection().insertOne(user);
            }
        }

        // Movie data

        UUID movieNo1ID = UUID.fromString("f3e66584-f793-4f5e-9dec-904ca00e2dd6");
        UUID movieNo2ID = UUID.fromString("9b9e1de2-099b-415d-96b4-f7cfc8897318");
        UUID movieNo3ID = UUID.fromString("b69b4714-e307-4ebf-b491-e3720f963f53");

        Movie movieNo1 = new Movie(movieNo1ID, "Pulp Fiction", 45.75, 1, 100);
        Movie movieNo2 = new Movie(movieNo2ID, "Cars", 30.50, 2, 50);
        Movie movieNo3 = new Movie(movieNo3ID, "Joker", 50.00, 3, 75);

        List<Movie> listOfMovies = List.of(movieNo1, movieNo2, movieNo3);
        for (Movie movie : listOfMovies) {
            Bson filter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movie.getMovieID());
            if (this.getMovieCollection().find(filter).first() == null) {
                this.getMovieCollection().insertOne(movie);
            }
        }

        // Creating tickets

        UUID ticketIDNo1 = UUID.fromString("a0ed1047-b56b-4e22-b797-c5a28df24d11");
        UUID ticketIDNo2 = UUID.fromString("1caa19c8-12c5-45ae-8019-ba93ba83a927");
        LocalDateTime movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(4).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime movieTimeNo2 = LocalDateTime.now().plusDays(4).plusHours(2).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);

        Ticket ticketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        Ticket ticketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, movieNo2.getMovieBasePrice(), clientNo2.getUserID(), movieNo2.getMovieID());

        this.getTicketCollection().insertOne(ticketNo1);
        this.getTicketCollection().insertOne(ticketNo2);
    }

    @PreDestroy
    private void restoreDatabaseState() {
        try {
            List<Ticket> listOfTickets = this.findAll();
            for (Ticket ticket : listOfTickets) {
                this.delete(ticket.getTicketID());
            }
        } catch (TicketRepositoryException exception) {
            logger.debug(exception.getMessage());
        }
        this.close();
    }

    public TicketRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);
        mongoDatabase.getCollection(ticketCollectionName).drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
    }

    @Override
    public Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException {
        Ticket ticket;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, clientID);
            User foundClientUser = getClientCollection().find(clientFilter).first();
            if (foundClientUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            } else if (!foundClientUser.isUserStatusActive()) {
                throw new ClientNotActiveException(MongoRepositoryMessages.ALLOCATION_NOT_POSSIBLE_SINCE_CLIENT_INACTIVE);
            }

            Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movieID);
            Movie foundMovie = getMovieCollection().find(movieFilter).first();
            if (foundMovie == null) {
                throw new MovieNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
            }

            Bson update = Updates.inc(MovieConstants.NUMBER_OF_AVAILABLE_SEATS, -1);
            getMovieCollection().updateOne(movieFilter, update);

            ticket = new Ticket(UUID.randomUUID(), movieTime, foundMovie.getMovieBasePrice(), clientID, movieID);
            getTicketCollection().insertOne(ticket);

            clientSession.commitTransaction();
        } catch (MongoException |
                 ObjectNullReferenceException |
                 ClientNotActiveException exception) {
            throw new TicketRepositoryCreateException(exception.getMessage(), exception);
        }
        return ticket;
    }

    @Override
    public Ticket findByUUID(UUID ticketID) throws TicketRepositoryException {
        try {
            return findTicket(ticketID);
        } catch (TicketNullReferenceException exception) {
            throw new TicketRepositoryTicketNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> findAll() throws TicketRepositoryException {
        List<Ticket> listOfAllTickets;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.empty();
            listOfAllTickets = findTickets(ticketFilter);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllTickets;
    }

    @Override
    public void update(Ticket ticket) throws TicketRepositoryException {
        try {
            Bson ticketFilter = Filters.eq("_id", ticket.getTicketID());
            Ticket updatedTicket = getTicketCollection().findOneAndReplace(ticketFilter, ticket);
            if (updatedTicket == null) {
                throw new TicketNullReferenceException(MongoRepositoryMessages.TICKET_DOC_FOR_TICKET_OBJ_NOT_FOUND);
            }
        } catch (MongoException | TicketNullReferenceException exception) {
            throw new TicketRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID ticketID) throws TicketRepositoryException {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.eq(TicketConstants.GENERAL_IDENTIFIER, ticketID);
            Ticket removedTicket = getTicketCollection().findOneAndDelete(ticketFilter);
            if (removedTicket != null) {
                Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, removedTicket.getMovieID());
                Bson update = Updates.inc(MovieConstants.NUMBER_OF_AVAILABLE_SEATS, 1);
                getMovieCollection().updateOne(movieFilter, update);
            } else {
                throw new TicketNullReferenceException(MongoRepositoryMessages.TICKET_DOC_OBJECT_NOT_FOUND);
            }
            clientSession.commitTransaction();
        } catch (MongoException | NullPointerException | ObjectNullReferenceException exception) {
            throw new TicketRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    private List<Ticket> findTickets(Bson ticketFilter) throws TicketRepositoryReadException {
        List<Ticket> listOfFoundTickets;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            listOfFoundTickets = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfFoundTickets;
    }
}