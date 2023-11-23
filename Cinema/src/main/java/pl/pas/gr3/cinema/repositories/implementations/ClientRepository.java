package pl.pas.gr3.cinema.repositories.implementations;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;
import pl.pas.gr3.cinema.exceptions.mapping.ClientDocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.mapping.DocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;
import pl.pas.gr3.cinema.mapping.mappers.users.AdminMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.ClientMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.StaffMapper;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClientRepository extends MongoRepository<Client> {

    private final String databaseName;

    public ClientRepository() {
        this.databaseName = "default";
        super.initDBConnection(this.databaseName);
        ValidationOptions validationOptions = new ValidationOptions().validator(
                Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "client_login", "client_password", "client_status_active"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the client object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "client_login": {
                                            "description": "String containing clients login to the cinema web app.",
                                            "bsonType": "string",
                                            "minLength": 8,
                                            "maxLength": 20,
                                            "pattern": "^[^\s]*$"
                                        }
                                        "client_password": {
                                            "description": "String containing clients password to the cinema web app.",
                                            "bsonType": "string",
                                            "minLength": 8,
                                            "maxLength": 40,
                                            "pattern": "^[^\s]*$"
                                        }
                                        "client_status_active": {
                                            "description": "Boolean flag indicating whether client is able to perform any action.",
                                            "bsonType": "bool"
                                        }
                                    }
                                }
                            }
                            """));
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(clientCollectionName, createCollectionOptions);
        IndexOptions indexOptions = new IndexOptions().unique(true);
        mongoDatabase.getCollection(clientCollectionName).createIndex(Indexes.ascending("client_login"), indexOptions);
    }

    @PostConstruct
    private void initializeDatabaseState() {
        UUID clientIDNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIDNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        UUID clientIDNo3 = UUID.fromString("30392328-2cae-4e76-abb8-b1aa8f58a9e4");
        UUID adminID = UUID.fromString("17dad3c7-7605-4808-bec5-d6f46abd23b8");
        UUID staffID = UUID.fromString("3d8ef63c-f99d-445c-85d0-4b14e68fc5a1");
        Client clientNo1 = new Client(clientIDNo1, "ClientLoginNo1", "ClientPasswordNo1");
        Client clientNo2 = new Client(clientIDNo2, "ClientLoginNo2", "ClientPasswordNo2");
        Client clientNo3 = new Client(clientIDNo3, "ClientLoginNo3", "ClientPasswordNo3");
        Admin admin = new Admin(adminID, "AdminLogin", "AdminPassword");
        Staff staff = new Staff(staffID, "StaffLogin", "StaffPassword");
        this.getClientCollection().insertOne(ClientMapper.toClientDoc(clientNo1));
        this.getClientCollection().insertOne(ClientMapper.toClientDoc(clientNo2));
        this.getClientCollection().insertOne(ClientMapper.toClientDoc(clientNo3));
        this.getClientCollection().insertOne(AdminMapper.toAdminDoc(admin));
        this.getClientCollection().insertOne(StaffMapper.toStaffDoc(staff));
    }

    @PreDestroy
    private void restoreDatabaseState() throws ClientRepositoryException {
        List<UUID> listOfAllUUIDs = this.findAllUUIDs();
        for (UUID clientID : listOfAllUUIDs) {
            this.delete(clientID);
        }
    }

    public ClientRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(clientCollectionName)) {
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
                                    "required": ["_id", "client_login", "client_password", "client_status_active"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the client object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "client_login": {
                                            "description": "String containing clients login to the cinema web app.",
                                            "bsonType": "string",
                                            "minLength": 8,
                                            "maxLength": 20,
                                            "pattern": "^[^\s]*$"
                                        }
                                        "client_password": {
                                            "description": "String containing clients password to the cinema web app.",
                                            "bsonType": "string",
                                            "minLength": 8,
                                            "maxLength": 40,
                                            "pattern": "^[^\s]*$"
                                        }
                                        "client_status_active": {
                                            "description": "Boolean flag indicating whether client is able to perform any action.",
                                            "bsonType": "bool"
                                        }
                                    }
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(clientCollectionName, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(clientCollectionName).createIndex(Indexes.ascending("client_login"), indexOptions);
        }
    }

    public Client createClient(String clientLogin, String clientPassword) throws ClientRepositoryException {
        Client client;
        try {
            client = new Client(UUID.randomUUID(), clientLogin, clientPassword);
            ClientDoc clientDoc = ClientMapper.toClientDoc(client);
            getClientCollection().insertOne(clientDoc);
        } catch (MongoException exception) {
            throw new ClientRepositoryCreateException(exception.getMessage(), exception);
        }
        return client;
    }

    public Admin createAdmin(String adminLogin, String adminPassword) throws ClientRepositoryException {
        Admin admin;
        try {
            admin = new Admin(UUID.randomUUID(), adminLogin, adminPassword);
            AdminDoc adminDoc = AdminMapper.toAdminDoc(admin);
            getClientCollection().insertOne(adminDoc);
        } catch (MongoException exception) {
            throw new ClientRepositoryCreateException(exception.getMessage(), exception);
        }
        return admin;
    }

    public Staff createStaff(String clientLogin, String clientPassword) throws ClientRepositoryException {
        Staff staff;
        try {
            staff = new Staff(UUID.randomUUID(), clientLogin, clientPassword);
            StaffDoc staffDoc = StaffMapper.toStaffDoc(staff);
            getClientCollection().insertOne(staffDoc);
        } catch (MongoException exception) {
            throw new ClientRepositoryCreateException(exception.getMessage(), exception);
        }
        return staff;
    }

    @Override
    public Client findByUUID(UUID clientID) throws ClientRepositoryReadException {
        Client client;
        try {
            client = super.findClient(clientID);
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    @Override
    public List<UUID> findAllUUIDs() throws ClientRepositoryReadException {
        List<UUID> listOfAllUUIDs = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.empty();
            for (ClientDoc clientDoc : getClientCollection().find(filter)) {
                listOfAllUUIDs.add(clientDoc.getClientID());
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllUUIDs;
    }

    @Override
    public List<Client> findAll() throws ClientRepositoryException {
        List<Client> listOfAllClients = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.empty();
            for (ClientDoc clientDoc : getClientCollection().find(filter)) {
                listOfAllClients.add(super.findClient(clientDoc.getClientID()));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllClients;
    }

    public List<Ticket> getListOfTicketsForClient(UUID clientID) {
        List<Ticket> listOfActiveTickets;
        List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("client_id", clientID)));
        listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
        return listOfActiveTickets;
    }

    public Client findByLogin(String loginValue) throws ClientRepositoryException {
        Client client;
        try {
            Bson clientFilter = Filters.eq("client_login", loginValue);
            ClientDoc foundClientDoc = getClientCollection().find(clientFilter).first();
            if (foundClientDoc != null) {
                client = super.findClient(foundClientDoc.getClientID());
            } else {
                throw new ClientDocNullReferenceException("Client object with given login could not be found in the database.");
            }
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    public List<Client> findAllMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Client> listOfMatchingClients;
        try {
            Bson clientFilter = Filters.regex("client_login", "^" + loginValue + ".*$");
            listOfMatchingClients = getClients(clientFilter);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingClients;
    }

    @Override
    public void update(Client client) throws ClientRepositoryException {
        try {
            ClientDoc newClientDoc = ClientMapper.toClientDoc(client);
            Bson clientFilter = Filters.eq("_id", client.getClientID());
            ClientDoc updatedClientDoc = getClientCollection().findOneAndReplace(clientFilter, newClientDoc);
            if (updatedClientDoc == null) {
                throw new ClientDocNullReferenceException("Client object representation for given client object could not be found in the database.");
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    public void delete(UUID clientID) throws ClientRepositoryException {
        try {
            Bson filter = Filters.eq("_id", clientID);
            ClientDoc removedClientDoc = getClientCollection().findOneAndDelete(filter);
            if (removedClientDoc == null) {
                throw new ClientDocNullReferenceException("Client object with given ID could not be found in the database.");
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    public void activate(Client client) throws ClientRepositoryException {
        client.setClientStatusActive(true);
        try {
            this.update(client);
        } catch (ClientRepositoryException exception) {
            throw new ClientActivationException("Client object representation for given client object could not be found in the database.");
        }
    }

    public void deactivate(Client client) throws ClientRepositoryException {
        client.setClientStatusActive(false);
        try {
            this.update(client);
        } catch (ClientRepositoryException exception) {
            throw new ClientDeactivationException("Client object representation for given client object could not be found in the database.");
        }
    }

    private List<Client> getClients(Bson clientFilter) throws ClientDocNullReferenceException {
        List<Client> listOfFoundClients = new ArrayList<>();
        for (ClientDoc clientDoc : getClientCollection().find(clientFilter)) {
            listOfFoundClients.add(super.findClient(clientDoc.getClientID()));
        }
        return listOfFoundClients;
    }
}
