package pl.pas.gr3.cinema.repositories.implementations;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import pl.pas.gr3.cinema.consts.model.MovieConstants;
import pl.pas.gr3.cinema.consts.model.TicketConstants;
import pl.pas.gr3.cinema.consts.model.UserConstants;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.MovieNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.ObjectNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.TicketNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.UserNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.UserTypeNotFoundException;
import pl.pas.gr3.cinema.messages.repositories.MongoRepositoryMessages;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.model.users.User;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MongoRepository implements Closeable {

    private final static ConnectionString connectionString = new ConnectionString("mongodb://mongodbnode1:27020, mongodbnode2:27021, mongodbnode3:27022");
    private final static MongoCredential mongoCredentials = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    protected final static String userCollectionName = MongoRepositoryConstants.USERS_COLLECTION_NAME;
    protected final static String movieCollectionName = MongoRepositoryConstants.TICKETS_COLLECTION_NAME;
    protected final static String ticketCollectionName = MongoRepositoryConstants.MOVIES_COLLECTION_NAME;

    protected final static Class<User> clientCollectionType = User.class;
    protected final static Class<Movie> movieCollectionType = Movie.class;
    protected final static Class<Ticket> ticketCollectionType = Ticket.class;

    private final ClassModel<User> userClassModel = ClassModel.builder(User.class).enableDiscriminator(true).build();
    private final ClassModel<Client> clientClassModel = ClassModel.builder(Client.class).enableDiscriminator(true).build();
    private final ClassModel<Admin> adminClassModel = ClassModel.builder(Admin.class).enableDiscriminator(true).build();
    private final ClassModel<Staff> staffClassModel = ClassModel.builder(Staff.class).enableDiscriminator(true).build();
    private final ClassModel<Movie> movieClassModel = ClassModel.builder(Movie.class).build();
    private final ClassModel<Ticket> ticketClassModel = ClassModel.builder(Ticket.class).build();

    private final PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
            userClassModel, clientClassModel, adminClassModel, staffClassModel, movieClassModel, ticketClassModel
    ).build();
    private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
            pojoCodecProvider,
            PojoCodecProvider.builder()
                    .automatic(true)
                    .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
                    .build()
    );

    protected MongoClient mongoClient;
    protected MongoDatabase mongoDatabase;

    // initDBConnection method

    protected void initDBConnection(String databaseName) {
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .credential(mongoCredentials)
                .readConcern(ReadConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }
    protected MongoCollection<User> getClientCollection() {
        return mongoDatabase.getCollection(userCollectionName, clientCollectionType);
    }

    protected MongoCollection<Document> getClientCollectionWithoutType() {
        return mongoDatabase.getCollection(userCollectionName);
    }

    protected MongoCollection<Movie> getMovieCollection() {
        return mongoDatabase.getCollection(movieCollectionName, movieCollectionType);
    }

    protected MongoCollection<Ticket> getTicketCollection() {
        return mongoDatabase.getCollection(ticketCollectionName, ticketCollectionType);
    }

    // Find client / movie / ticket by ID

    protected User findUser(UUID userID) throws UserNullReferenceException {
        Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, userID);
        User user = getClientCollection().find(clientFilter).first();
        if (user != null) {
            return user;
        } else {
            throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
        }
    }

    protected Movie findMovie(UUID movieID) throws MovieNullReferenceException {
        Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movieID);
        Movie movie = getMovieCollection().find(movieFilter).first();
        if (movie != null) {
            return movie;
        } else {
            throw new MovieNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
        }
    }

    protected Ticket findTicket(UUID ticketID) throws TicketNullReferenceException {
        Bson ticketFilter = Filters.eq(TicketConstants.GENERAL_IDENTIFIER, ticketID);
        Ticket ticket = getTicketCollection().find(ticketFilter).first();
        if (ticket != null) {
            return ticket;
        } else {
            throw new TicketNullReferenceException(MongoRepositoryMessages.TICKET_DOC_OBJECT_NOT_FOUND);
        }
    }

    public List<Ticket> findTicketsWithAggregate(List<Bson> listOfFilters) {
        return getTicketCollection().aggregate(listOfFilters).into(new ArrayList<>());
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
