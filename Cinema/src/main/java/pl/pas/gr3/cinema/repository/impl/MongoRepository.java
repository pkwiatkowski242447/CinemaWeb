package pl.pas.gr3.cinema.repository.impl;

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
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.MovieNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.TicketNotFoundException;
import pl.pas.gr3.cinema.util.consts.model.MovieConstants;
import pl.pas.gr3.cinema.util.consts.model.TicketConstants;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.util.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MongoRepository implements Closeable {

    private final static ConnectionString connectionString = new ConnectionString(
        "mongodb://admin:P%40ssw0rd%21@mongo_node1:27020,mongo_node2:27021,mongo_node3:27022/?authSource=admin&replicaSet=pasReplicaSet"
    );

    protected final static String userCollectionName = MongoRepositoryConstants.USERS_COLLECTION_NAME;
    protected final static String movieCollectionName = MongoRepositoryConstants.MOVIES_COLLECTION_NAME;
    protected final static String ticketCollectionName = MongoRepositoryConstants.TICKETS_COLLECTION_NAME;

    protected final static Class<Account> clientCollectionType = Account.class;
    protected final static Class<Movie> movieCollectionType = Movie.class;
    protected final static Class<Ticket> ticketCollectionType = Ticket.class;

    private final ClassModel<Account> userClassModel = ClassModel.builder(Account.class).enableDiscriminator(true).build();
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
            .applyConnectionString(connectionString)
            .readConcern(ReadConcern.MAJORITY)
            .readPreference(ReadPreference.primary())
            .writeConcern(WriteConcern.MAJORITY)
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .codecRegistry(CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry
            ))
            .build();

        mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    protected MongoCollection<Account> getClientCollection() {
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

    protected Account findUser(UUID userID) {
        Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, userID);
        Account account = getClientCollection().find(clientFilter).first();
        if (account == null) throw new AccountNotFoundException();
        return account;
    }

    protected Movie findMovie(UUID movieID) {
        Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movieID);
        Movie movie = getMovieCollection().find(movieFilter).first();
        if (movie == null) throw new MovieNotFoundException();
        return movie;
    }

    protected Ticket findTicket(UUID ticketID) {
        Bson ticketFilter = Filters.eq(TicketConstants.GENERAL_IDENTIFIER, ticketID);
        Ticket ticket = getTicketCollection().find(ticketFilter).first();
        if (ticket == null) throw new TicketNotFoundException();
        return ticket;
    }

    public List<Ticket> findTicketsWithAggregate(List<Bson> listOfFilters) {
        return getTicketCollection().aggregate(listOfFilters).into(new ArrayList<>());
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
