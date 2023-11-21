package pl.pas.gr3.cinema.repositories;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.ValidationOptions;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import pl.pas.gr3.cinema.exceptions.mapping.*;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.mappers.MovieMapper;
import pl.pas.gr3.cinema.mapping.mappers.TicketMapper;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketRepository extends MongoRepository<Ticket> {

    public TicketRepository(String databaseName) {
        super.initDBConnection(databaseName);

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(ticketCollectionName)) {
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
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
        }
    }

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

    public List<Ticket> findAllActiveTickets() throws TicketRepositoryReadException {
        List<Ticket> listOfAllActiveTickets;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.eq("reservation_end", BsonNull.VALUE);
            listOfAllActiveTickets = findTickets(ticketFilter);
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllActiveTickets;
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
