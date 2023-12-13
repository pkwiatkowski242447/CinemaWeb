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
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.mapping.*;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.*;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientNotActiveException;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.mappers.MovieMapper;
import pl.pas.gr3.cinema.mapping.mappers.TicketMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.AdminMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.ClientMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.StaffMapper;
import pl.pas.gr3.cinema.messages.repositories.MongoRepositoryMessages;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
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
                                    "required": ["_id", "movie_time", "movie_final_price", "client_id", "movie_id"],
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
                                        "movie_final_price": {
                                            "description": "Double value holding final price of the ticket, that is after applying all discounts.",
                                            "bsonType": "double"
                                        }
                                        "client_id": {
                                            "description": "Id of the client object representation in the database.",
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
        UUID adminID = UUID.fromString("17dad3c7-7605-4808-bec5-d6f46abd23b8");
        UUID staffID = UUID.fromString("3d8ef63c-f99d-445c-85d0-4b14e68fc5a1");
        Client clientNo1 = new Client(clientIDNo1, "NewFirstClientLogin", "ClientPasswordNo1");
        Client clientNo2 = new Client(clientIDNo2, "NewSecondClientLogin", "ClientPasswordNo2");
        Client clientNo3 = new Client(clientIDNo3, "NewThirdClientLogin", "ClientPasswordNo3");
        Admin admin = new Admin(adminID, "NewSecretAdminLogin", "AdminPassword");
        Staff staff = new Staff(staffID, "NewSecretStaffLogin", "StaffPassword");

        List<Client> listOfClients = List.of(clientNo1, clientNo2, clientNo3, admin, staff);
        for (Client client : listOfClients) {
            Bson filter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, client.getClientID());
            if (this.getClientCollection().find(filter).first() == null && client.getClass().equals(Client.class)) {
                this.getClientCollection().insertOne(ClientMapper.toClientDoc(client));
            } else if (this.getClientCollection().find(filter).first() == null && client.getClass().equals(Admin.class)) {
                this.getClientCollection().insertOne(AdminMapper.toAdminDoc(client));
            } else if (this.getClientCollection().find(filter).first() == null && client.getClass().equals(Staff.class)) {
                this.getClientCollection().insertOne(StaffMapper.toStaffDoc(client));
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
            Bson filter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, movie.getMovieID());
            if (this.getMovieCollection().find(filter).first() == null) {
                this.getMovieCollection().insertOne(MovieMapper.toMovieDoc(movie));
            }
        }

        // Creating tickets

        UUID ticketIDNo1 = UUID.fromString("a0ed1047-b56b-4e22-b797-c5a28df24d11");
        UUID ticketIDNo2 = UUID.fromString("1caa19c8-12c5-45ae-8019-ba93ba83a927");
        LocalDateTime movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(4).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime movieTimeNo2 = LocalDateTime.now().plusDays(4).plusHours(2).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);

        try {
            Ticket ticketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, clientNo1, movieNo1, TicketType.NORMAL);
            Ticket ticketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, clientNo2, movieNo2, TicketType.REDUCED);
            this.getTicketCollection().insertOne(TicketMapper.toTicketDoc(ticketNo1));
            this.getTicketCollection().insertOne(TicketMapper.toTicketDoc(ticketNo2));
        } catch (TicketCreateException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @PreDestroy
    private void restoreDatabaseState() {
        try {
            List<UUID> listOfAllUUIDs = this.findAllUUIDs();
            for (UUID ticketID : listOfAllUUIDs) {
                this.delete(ticketID);
            }
        } catch (TicketRepositoryException exception) {
            logger.debug(exception.getMessage());
        }
        this.close();
    }

    public TicketRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);
        this.getTicketCollection().drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
    }

    @Override
    public Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID, TicketType ticketType) throws TicketRepositoryException {
        Ticket ticket;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson clientFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, clientID);
            ClientDoc foundClientDoc = getClientCollection().find(clientFilter).first();
            Client client;
            if (foundClientDoc == null) {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            } else if (!foundClientDoc.isClientStatusActive()) {
                throw new ClientNotActiveException(MongoRepositoryMessages.ALLOCATION_NOT_POSSIBLE_SINCE_CLIENT_INACTIVE);
            } else {
                client = super.findClient(clientID);
            }

            Bson movieFilter = Filters.eq("_id", movieID);
            MovieDoc foundMovieDoc = getMovieCollection().find(movieFilter).first();
            Movie movie;
            if (foundMovieDoc != null) {
                movie = MovieMapper.toMovie(foundMovieDoc);
            } else {
                throw new MovieDocNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
            }

            ticket = new Ticket(UUID.randomUUID(), movieTime, client, movie, ticketType);
            TicketDoc ticketDoc = TicketMapper.toTicketDoc(ticket);
            getTicketCollection().insertOne(ticketDoc);

            MovieDoc movieDoc = MovieMapper.toMovieDoc(movie);
            getMovieCollection().findOneAndReplace(movieFilter, movieDoc);
            clientSession.commitTransaction();
        } catch (MongoException |
                 TicketCreateException |
                 DocNullReferenceException |
                 ClientNotActiveException exception) {
            throw new TicketRepositoryCreateException(exception.getMessage(), exception);
        }
        return ticket;
    }

    @Override
    public Ticket findByUUID(UUID ticketID) throws TicketRepositoryException {
        Ticket ticket;
        try {
            TicketDoc foundTicketDoc = findTicketDoc(ticketID);
            ticket = this.getTicketFromTicketDoc(foundTicketDoc);
        } catch (TicketDocNullReferenceException exception) {
            throw new TicketRepositoryTicketNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return ticket;
    }

    @Override
    public List<UUID> findAllUUIDs() throws TicketRepositoryException {
        List<UUID> listOfAllUUIDs = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilterBson = Filters.empty();
            for (TicketDoc ticketDoc : getTicketCollection().find(ticketFilterBson)) {
                listOfAllUUIDs.add(ticketDoc.getTicketID());
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllUUIDs;
    }

    @Override
    public List<Ticket> findAll() throws TicketRepositoryException {
        List<Ticket> listOfAllTickets;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.empty();
            listOfAllTickets = findTickets(ticketFilter);
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllTickets;
    }

    @Override
    public void update(Ticket ticket) throws TicketRepositoryException {
        try {
            TicketDoc ticketDoc = TicketMapper.toTicketDoc(ticket);
            Bson ticketFilter = Filters.eq("_id", ticket.getTicketID());
            TicketDoc updatedTicketDoc = getTicketCollection().findOneAndReplace(ticketFilter, ticketDoc);
            if (updatedTicketDoc == null) {
                throw new TicketDocNullReferenceException(MongoRepositoryMessages.TICKET_DOC_FOR_TICKET_OBJ_NOT_FOUND);
            }
        } catch (MongoException | DocNullReferenceException exception) {
            throw new TicketRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID ticketID) throws TicketRepositoryException {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, ticketID);
            TicketDoc removedTicketDoc = getTicketCollection().findOneAndDelete(ticketFilter);
            if (removedTicketDoc != null) {
                Bson movieFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, removedTicketDoc.getMovieID());
                Bson updates = Updates.inc(MongoRepositoryConstants.NUMBER_OF_AVAILABLE_SEATS, 1);
                MovieDoc updatedMovieDoc = getMovieCollection().findOneAndUpdate(movieFilter, updates);
                if (updatedMovieDoc == null) {
                    throw new MovieDocNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
                }
            } else {
                throw new TicketDocNullReferenceException(MongoRepositoryMessages.TICKET_DOC_OBJECT_NOT_FOUND);
            }
            clientSession.commitTransaction();
        } catch (MongoException | NullPointerException | DocNullReferenceException exception) {
            throw new TicketRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    private List<Ticket> findTickets(Bson ticketFilter) {
        List<Ticket> listOfFoundTickets = new ArrayList<>();
        for (TicketDoc ticketDoc : getTicketCollection().find(ticketFilter)) {
            ClientDoc foundClientDoc = findClientDoc(ticketDoc.getClientID());
            listOfFoundTickets.add(TicketMapper.toTicket(ticketDoc,
                    foundClientDoc,
                    findMovieDoc(ticketDoc.getMovieID())));
        }
        return listOfFoundTickets;
    }
}