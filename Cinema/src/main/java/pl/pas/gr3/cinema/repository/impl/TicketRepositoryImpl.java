package pl.pas.gr3.cinema.repository.impl;

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
import pl.pas.gr3.cinema.exception.bad_request.ClientNotActiveException;
import pl.pas.gr3.cinema.exception.bad_request.TicketCreateException;
import pl.pas.gr3.cinema.exception.bad_request.TicketDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.TicketUpdateException;
import pl.pas.gr3.cinema.exception.not_found.ClientNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.MovieNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.TicketNotFoundException;
import pl.pas.gr3.cinema.util.consts.model.MovieConstants;
import pl.pas.gr3.cinema.util.consts.model.TicketConstants;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.repository.api.TicketRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class TicketRepositoryImpl extends MongoRepository implements TicketRepository {

    private final String databaseName;
    private static final Logger logger = LoggerFactory.getLogger(TicketRepositoryImpl.class);
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
                            "description": "Id of the account object representation in the database.",
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

    public TicketRepositoryImpl() {
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
        /* Client data */
        UUID clientIdNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIdNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        UUID clientIdNo3 = UUID.fromString("30392328-2cae-4e76-abb8-b1aa8f58a9e4");

        UUID adminIdNo1 = UUID.fromString("17dad3c7-7605-4808-bec5-d6f46abd23b8");
        UUID adminIdNo2 = UUID.fromString("ca857499-cdd5-4de3-a8d2-1ba7afcec2ef");
        UUID adminIdNo3 = UUID.fromString("07f97385-a2a3-474e-af61-f53d14a64198");

        UUID staffIdNo1 = UUID.fromString("67a85b0f-d063-4c9b-b223-fcc606c00f2f");
        UUID staffIdNo2 = UUID.fromString("3d8ef63c-f99d-445c-85d0-4b14e68fc5a1");
        UUID staffIdNo3 = UUID.fromString("86e394dd-e192-4390-b4e4-76029c879857");

        Client clientNo1 = new Client(clientIdNo1, "NewClientLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Client clientNo2 = new Client(clientIdNo2, "NewClientLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Client clientNo3 = new Client(clientIdNo3, "NewClientLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        Admin adminNo1 = new Admin(adminIdNo1, "NewAdminLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Admin adminNo2 = new Admin(adminIdNo2, "NewAdminLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Admin adminNo3 = new Admin(adminIdNo3, "NewAdminLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        Staff staffNo1 = new Staff(staffIdNo1, "NewStaffLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Staff staffNo2 = new Staff(staffIdNo2, "NewStaffLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Staff staffNo3 = new Staff(staffIdNo3, "NewStaffLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        List<Account> accounts = List.of(clientNo1, clientNo2, clientNo3, adminNo1, adminNo2, adminNo3, staffNo1, staffNo2, staffNo3);
        accounts.forEach(account -> {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, account.getId());
            if (getClientCollection().find(filter).first() == null &&
                account.getClass().equals(Client.class)) getClientCollection().insertOne(account);

            else if (getClientCollection().find(filter).first() == null &&
                account.getClass().equals(Admin.class)) getClientCollection().insertOne(account);

            else if (getClientCollection().find(filter).first() == null &&
                account.getClass().equals(Staff.class)) getClientCollection().insertOne(account);
        });

        /* Movie data */
        UUID movieIdNo1 = UUID.fromString("f3e66584-f793-4f5e-9dec-904ca00e2dd6");
        UUID movieIdNo2 = UUID.fromString("9b9e1de2-099b-415d-96b4-f7cfc8897318");
        UUID movieIdNo3 = UUID.fromString("b69b4714-e307-4ebf-b491-e3720f963f53");

        Movie movieNo1 = new Movie(movieIdNo1, "Pulp Fiction", 45.75, 1, 100);
        Movie movieNo2 = new Movie(movieIdNo2, "Cars", 30.50, 2, 50);
        Movie movieNo3 = new Movie(movieIdNo3, "Joker", 50.00, 3, 75);

        List<Movie> movies = List.of(movieNo1, movieNo2, movieNo3);
        movies.forEach(movie -> {
            Bson filter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movie.getId());
            if (this.getMovieCollection().find(filter).first() == null)
                this.getMovieCollection().insertOne(movie);
        });

        /* Ticket data */
        UUID ticketIdNo1 = UUID.fromString("a0ed1047-b56b-4e22-b797-c5a28df24d11");
        UUID ticketIdNo2 = UUID.fromString("1caa19c8-12c5-45ae-8019-ba93ba83a927");
        LocalDateTime movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(4).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime movieTimeNo2 = LocalDateTime.now().plusDays(4).plusHours(2).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);

        Ticket ticketNo1 = new Ticket(ticketIdNo1, movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        Ticket ticketNo2 = new Ticket(ticketIdNo2, movieTimeNo2, movieNo2.getBasePrice(), clientNo2.getId(), movieNo2.getId());

        this.getTicketCollection().insertOne(ticketNo1);
        this.getTicketCollection().insertOne(ticketNo2);
    }

    @PreDestroy
    private void restoreDatabaseState() {
        try {
            List<Ticket> tickets = findAll();
            tickets.forEach(ticket -> delete(ticket.getId()));
        } catch (Exception exception) {
            logger.debug(exception.getMessage());
        }
        close();
    }

    public TicketRepositoryImpl(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);
        mongoDatabase.getCollection(ticketCollectionName).drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
    }

    @Override
    public Ticket create(LocalDateTime movieTime, UUID clientId, UUID movieId) {
        Ticket ticket;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, clientId);
            Account foundClientAccount = getClientCollection().find(clientFilter).first();
            if (foundClientAccount == null) throw new ClientNotFoundException();
            else if (!foundClientAccount.isActive()) throw new ClientNotActiveException();

            Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movieId);
            Movie foundMovie = getMovieCollection().find(movieFilter).first();
            if (foundMovie == null) throw new MovieNotFoundException();

            Bson update = Updates.inc(MovieConstants.NUMBER_OF_AVAILABLE_SEATS, -1);
            getMovieCollection().updateOne(movieFilter, update);

            ticket = new Ticket(UUID.randomUUID(), movieTime, foundMovie.getBasePrice(), clientId, movieId);
            getTicketCollection().insertOne(ticket);

            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketCreateException(exception);
        }
        return ticket;
    }

    @Override
    public Ticket findByUUID(UUID ticketId) {
        try {
            return findTicket(ticketId);
        } catch (MongoException exception) {
            throw new TicketNotFoundException(exception);
        }
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> listOfAllTickets;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.empty();
            listOfAllTickets = findTickets(ticketFilter);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketNotFoundException(exception);
        }
        return listOfAllTickets;
    }

    @Override
    public void update(Ticket ticket) {
        try {
            Bson ticketFilter = Filters.eq("_id", ticket.getId());
            Ticket updatedTicket = getTicketCollection().findOneAndReplace(ticketFilter, ticket);
            if (updatedTicket == null) throw new TicketNotFoundException();
        } catch (MongoException exception) {
            throw new TicketUpdateException(exception);
        }
    }

    @Override
    public void delete(UUID ticketId) {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.eq(TicketConstants.GENERAL_IDENTIFIER, ticketId);
            Ticket removedTicket = getTicketCollection().findOneAndDelete(ticketFilter);
            if (removedTicket != null) {
                Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, removedTicket.getMovieId());
                Bson update = Updates.inc(MovieConstants.NUMBER_OF_AVAILABLE_SEATS, 1);
                getMovieCollection().updateOne(movieFilter, update);
            } else throw new TicketNotFoundException();
            clientSession.commitTransaction();
        } catch (MongoException | NullPointerException exception) {
            throw new TicketDeleteException(exception);
        }
    }

    /* OTHER */

    private List<Ticket> findTickets(Bson ticketFilter) {
        List<Ticket> listOfFoundTickets;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            listOfFoundTickets = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketNotFoundException(exception);
        }
        return listOfFoundTickets;
    }
}