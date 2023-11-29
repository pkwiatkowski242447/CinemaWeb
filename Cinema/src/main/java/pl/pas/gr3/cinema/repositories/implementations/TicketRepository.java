package pl.pas.gr3.cinema.repositories.implementations;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.ValidationOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.mapping.*;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryDeleteException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryReadException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryUpdateException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientNotActiveException;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.mappers.MovieMapper;
import pl.pas.gr3.cinema.mapping.mappers.TicketMapper;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.interfaces.TicketRepositoryInterface;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
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
        UUID clientIDNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIDNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        Client clientNo1 = new Client(clientIDNo1, "ClientLoginNo1", "ClientPasswordNo1");
        Client clientNo2 = new Client(clientIDNo2, "ClientLoginNo2", "ClientPasswordNo2");
        UUID movieNo1ID = UUID.fromString("f3e66584-f793-4f5e-9dec-904ca00e2dd6");
        UUID movieNo2ID = UUID.fromString("9b9e1de2-099b-415d-96b4-f7cfc8897318");
        Movie movieNo1 = new Movie(movieNo1ID, "Pulp Fiction", 45.75, 1, 100);
        Movie movieNo2 = new Movie(movieNo2ID, "Cars", 30.50, 2, 50);
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
            Bson clientFilter = Filters.eq("_id", clientID);
            ClientDoc foundClientDoc = getClientCollection().find(clientFilter).first();
            Client client;
            if (foundClientDoc == null) {
                throw new ClientDocNullReferenceException("Client object with given ID could not be found in the database.");
            } else if (!foundClientDoc.isClientStatusActive()) {
                throw new ClientNotActiveException("Given client is not active, and therefore could not crete resource allocation.");
            } else {
                client = super.findClient(clientID);
            }

            Bson movieFilter = Filters.eq("_id", movieID);
            MovieDoc foundMovieDoc = getMovieCollection().find(movieFilter).first();
            Movie movie;
            if (foundMovieDoc != null) {
                movie = MovieMapper.toMovie(foundMovieDoc);
            } else {
                throw new MovieDocNullReferenceException("Movie object with given ID could not be found in the database.");
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
        } catch (MongoException | DocNullReferenceException exception) {
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
                throw new TicketDocNullReferenceException("Ticket object representation for given ticket object could not be found in the database.");
            }
        } catch (MongoException | DocNullReferenceException exception) {
            throw new TicketRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID ticketID) throws TicketRepositoryException {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.eq("_id", ticketID);
            TicketDoc removedTicketDoc = getTicketCollection().findOneAndDelete(ticketFilter);
            if (removedTicketDoc != null) {
                Bson movieFilter = Filters.eq("_id", removedTicketDoc.getMovieID());
                Bson updates = Updates.inc("number_of_available_seats", 1);
                MovieDoc updatedMovieDoc = getMovieCollection().findOneAndUpdate(movieFilter, updates);
                if (updatedMovieDoc == null) {
                    throw new MovieDocNullReferenceException("Movie object for given ticket ID could not be found in the database.");
                }
            } else {
                throw new TicketDocNullReferenceException("Ticket object with given ID could not be found in the database.");
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