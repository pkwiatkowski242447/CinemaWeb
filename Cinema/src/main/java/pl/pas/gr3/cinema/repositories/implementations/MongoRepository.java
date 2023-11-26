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
import pl.pas.gr3.cinema.exceptions.mapping.ClientDocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.mapping.DocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.mapping.MovieDocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.mapping.TicketDocNullReferenceException;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;
import pl.pas.gr3.cinema.mapping.mappers.TicketMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.AdminMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.ClientMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.StaffMapper;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MongoRepository implements Closeable {

    private final static ConnectionString connectionString = new ConnectionString("mongodb://mongodbnode1:27017, mongodbnode2:27018, mongodbnode3:27019");
    private final static MongoCredential mongoCredentials = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    protected final static String clientCollectionName = "clients";
    protected final static String movieCollectionName = "movies";
    protected final static String ticketCollectionName = "tickets";

    protected final static Class<ClientDoc> clientCollectionType = ClientDoc.class;
    protected final static Class<MovieDoc> movieCollectionType = MovieDoc.class;
    protected final static Class<TicketDoc> ticketCollectionType = TicketDoc.class;

    private final ClassModel<ClientDoc> clientDocClassModel = ClassModel.builder(ClientDoc.class).enableDiscriminator(true).build();
    private final ClassModel<AdminDoc> adminDocClassModel = ClassModel.builder(AdminDoc.class).enableDiscriminator(true).build();
    private final ClassModel<StaffDoc> staffDocClassModel = ClassModel.builder(StaffDoc.class).enableDiscriminator(true).build();
    private final ClassModel<MovieDoc> movieDocClassModel = ClassModel.builder(MovieDoc.class).build();
    private final ClassModel<TicketDoc> ticketDocClassModel = ClassModel.builder(TicketDoc.class).build();

    private final PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
            clientDocClassModel, adminDocClassModel, staffDocClassModel, movieDocClassModel, ticketDocClassModel
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
    protected MongoCollection<ClientDoc> getClientCollection() {
        return mongoDatabase.getCollection(clientCollectionName, clientCollectionType);
    }

    protected MongoCollection<Document> getClientCollectionWithoutType() {
        return mongoDatabase.getCollection(clientCollectionName);
    }

    protected MongoCollection<MovieDoc> getMovieCollection() {
        return mongoDatabase.getCollection(movieCollectionName, movieCollectionType);
    }

    protected MongoCollection<TicketDoc> getTicketCollection() {
        return mongoDatabase.getCollection(ticketCollectionName, ticketCollectionType);
    }

    // Find client / movie / ticket by ID

    protected ClientDoc findClientDoc(UUID clientDocID) {
        Bson clientFilter = Filters.eq("_id", clientDocID);
        ClientDoc clientDoc = getClientCollection().find(clientFilter).first();
        if (clientDoc != null) {
            return clientDoc;
        } else {
            throw new ClientDocNullReferenceException("Client object with given ID could not be found in the database.");
        }
    }

    protected MovieDoc findMovieDoc(UUID movieDocID) {
        Bson movieFilter = Filters.eq("_id", movieDocID);
        MovieDoc movieDoc = getMovieCollection().find(movieFilter).first();
        if (movieDoc != null) {
            return movieDoc;
        } else {
            throw new MovieDocNullReferenceException("Movie object with given ID could not be found in the database.");
        }
    }

    protected TicketDoc findTicketDoc(UUID ticketDocID) {
        Bson ticketFilter = Filters.eq("_id", ticketDocID);
        TicketDoc ticketDoc = getTicketCollection().find(ticketFilter).first();
        if (ticketDoc != null) {
            return ticketDoc;
        } else {
            throw new TicketDocNullReferenceException("Ticket object with given ID could not be found in the database.");
        }
    }

    protected Client findClient(UUID clientID) {
        Bson documentFilter = Filters.eq("_id", clientID);
        Document clientDocument = getClientCollectionWithoutType().find(documentFilter).first();
        Client client;
        if (clientDocument != null) {
            UUID foundClientID = (UUID) clientDocument.get("_id");
            String foundClientLogin = clientDocument.getString("client_login");
            String foundClientPassword = clientDocument.getString("client_password");
            boolean foundClientStatusActive = clientDocument.getBoolean("client_status_active");
            switch(clientDocument.getString("_clazz")) {
                case "admin": {
                    client = AdminMapper.toAdmin(new AdminDoc(foundClientID, foundClientLogin, foundClientPassword, foundClientStatusActive));
                    break;
                }
                case "staff": {
                    client = StaffMapper.toStaff(new StaffDoc(foundClientID, foundClientLogin, foundClientPassword, foundClientStatusActive));
                    break;
                }
                default: {
                    client = ClientMapper.toClient(new ClientDoc(foundClientID, foundClientLogin, foundClientPassword, foundClientStatusActive));
                }
            }
        } else {
            throw new DocNullReferenceException("Document object with given ID could not be found in client collection in the database.");
        }
        return client;
    }

    protected Ticket getTicketFromTicketDoc(TicketDoc ticketDoc) throws DocNullReferenceException {
        Ticket ticket;
        Bson movieFilter = Filters.eq("_id", ticketDoc.getMovieID());
        Bson clientFilter = Filters.eq("_id", ticketDoc.getClientID());
        MovieDoc foundMovieDoc = getMovieCollection().find(movieFilter).first();
        ClientDoc foundClientDoc = getClientCollection().find(clientFilter).first();
        if (foundMovieDoc == null) {
            throw new MovieDocNullReferenceException("Movie object for given ticketDoc could not be found in the database.");
        } else if (foundClientDoc == null) {
            throw new ClientDocNullReferenceException("Client object for given clientDoc could not be found in the database.");
        } else {
            ticket = TicketMapper.toTicket(ticketDoc, foundClientDoc, foundMovieDoc);
        }
        return ticket;
    }

    public List<Ticket> findTicketsWithAggregate(List<Bson> listOfFilters) {
        List<Ticket> listOfTickets = new ArrayList<>();
        AggregateIterable<TicketDoc> aggregate = getTicketCollection().aggregate(listOfFilters);
        for (TicketDoc ticketDoc : aggregate) {
            listOfTickets.add(getTicketFromTicketDoc(ticketDoc));
        }
        return listOfTickets;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
