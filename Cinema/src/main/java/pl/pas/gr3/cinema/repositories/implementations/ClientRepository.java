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
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryDeleteException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryReadException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryUpdateException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientActivationException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientDeactivationException;
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
import pl.pas.gr3.cinema.repositories.interfaces.ClientRepositoryInterface;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClientRepository extends MongoRepository implements ClientRepositoryInterface {

    private final String databaseName;
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
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

    public ClientRepository() {
        this.databaseName = "default";
        super.initDBConnection(this.databaseName);
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
        this.getClientCollection().drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(clientCollectionName, createCollectionOptions);
        IndexOptions indexOptions = new IndexOptions().unique(true);
        mongoDatabase.getCollection(clientCollectionName).createIndex(Indexes.ascending("client_login"), indexOptions);
    }

    @Override
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

    @Override
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

    @Override
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
    public List<Client> findAllClients() throws ClientRepositoryException {
        List<Client> listOfAllClients = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq("_clazz", "client");
            for (ClientDoc clientDoc : getClientCollection().find(filter)) {
                listOfAllClients.add(super.findClient(clientDoc.getClientID()));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllClients;
    }

    @Override
    public List<Client> findAllAdmins() throws ClientRepositoryException {
        List<Client> listOfAllAdmins = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq("_clazz", "admin");
            for (ClientDoc clientDoc : getClientCollection().find(filter)) {
                listOfAllAdmins.add(super.findClient(clientDoc.getClientID()));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllAdmins;
    }

    @Override
    public List<Client> findAllStaffs() throws ClientRepositoryException {
        List<Client> listOfAllStaff = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq("_clazz", "staff");
            for (ClientDoc clientDoc : getClientCollection().find(filter)) {
                listOfAllStaff.add(super.findClient(clientDoc.getClientID()));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllStaff;
    }

    public List<Ticket> getListOfTicketsForClient(UUID clientID) {
        List<Ticket> listOfActiveTickets;
        List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("client_id", clientID)));
        listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
        return listOfActiveTickets;
    }

    @Override
    public Client findClientByLogin(String loginValue) throws ClientRepositoryException {
        Client client;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("_clazz", "client")),
                    Aggregates.match(Filters.eq("client_login", loginValue)));
            ClientDoc foundClientDoc = getClientCollection().aggregate(listOfFilters).first();
            if (foundClientDoc != null) {
                client = ClientMapper.toClient(foundClientDoc);
            } else {
                throw new ClientDocNullReferenceException("Client object with given login could not be found in the database.");
            }
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    @Override
    public List<Client> findAllClientsMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Client> listOfMatchingClients;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("_clazz", "client")),
                    Aggregates.match(Filters.regex("client_login", "^" + loginValue + ".*$")));
            listOfMatchingClients = getClients(listOfFilters);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingClients;
    }

    @Override
    public Admin findAdminByLogin(String loginValue) throws ClientRepositoryException {
        Admin admin;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("_clazz", "admin")),
                    Aggregates.match(Filters.eq("client_login", loginValue)));
            ClientDoc foundAdminDoc = getClientCollection().aggregate(listOfFilters).first();
            if (foundAdminDoc != null) {
                admin = AdminMapper.toAdmin(foundAdminDoc);
            } else {
                throw new ClientDocNullReferenceException("Admin object with given login could not be found in the database.");
            }
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return admin;
    }

    @Override
    public List<Client> findAllAdminsMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Client> listOfMatchingAdmins;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("_clazz", "admin")),
                    Aggregates.match(Filters.regex("client_login", "^" + loginValue + ".*$")));
            listOfMatchingAdmins = getClients(listOfFilters);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingAdmins;
    }

    @Override
    public Staff findStaffByLogin(String loginValue) throws ClientRepositoryException {
        Staff staff;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("_clazz", "staff")),
                    Aggregates.match(Filters.eq("client_login", loginValue)));
            ClientDoc foundStaffDoc = getClientCollection().aggregate(listOfFilters).first();
            if (foundStaffDoc != null) {
                staff = StaffMapper.toStaff(foundStaffDoc);
            } else {
                throw new ClientDocNullReferenceException("Staff object with given login could not be found in the database.");
            }
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return staff;
    }

    @Override
    public List<Client> findAllStaffsMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Client> listOfMatchingStaffs;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq("_clazz", "staff")),
                    Aggregates.match(Filters.regex("client_login", "^" + loginValue + ".*$")));
            listOfMatchingStaffs = getClients(listOfFilters);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingStaffs;
    }

    @Override
    public void updateClient(Client client) throws ClientRepositoryException {
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

    @Override
    public void updateAdmin(Admin admin) throws ClientRepositoryException {
        try {
            ClientDoc newAdminDoc = AdminMapper.toAdminDoc(admin);
            Bson adminFilter = Filters.eq("_id", admin.getClientID());
            ClientDoc updatedAdminDoc = getClientCollection().findOneAndReplace(adminFilter, newAdminDoc);
            if (updatedAdminDoc == null) {
                throw new ClientDocNullReferenceException("Admin object representation for given client object could not be found in the database.");
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateStaff(Staff staff) throws ClientRepositoryException {
        try {
            ClientDoc newStaffDoc = StaffMapper.toStaffDoc(staff);
            Bson staffFilter = Filters.eq("_id", staff.getClientID());
            ClientDoc updatedStaffDoc = getClientCollection().findOneAndReplace(staffFilter, newStaffDoc);
            if (updatedStaffDoc == null) {
                throw new ClientDocNullReferenceException("Staff object representation for given client object could not be found in the database.");
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
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

    @Override
    public void activate(Client client) throws ClientRepositoryException {
        client.setClientStatusActive(true);
        try {
            if (client.getClass().equals(Admin.class)) {
                this.updateAdmin(AdminMapper.toAdmin(ClientMapper.toClientDoc(client)));
            } else if (client.getClass().equals(Staff.class)) {
                this.updateStaff(StaffMapper.toStaff(ClientMapper.toClientDoc(client)));
            } else {
                this.updateClient(client);
            }
        } catch (ClientRepositoryException exception) {
            throw new ClientActivationException("Client object representation for given client object could not be found in the database.");
        }
    }

    @Override
    public void deactivate(Client client) throws ClientRepositoryException {
        client.setClientStatusActive(false);
        try {
            if (client.getClass().equals(Admin.class)) {
                this.updateAdmin(AdminMapper.toAdmin(ClientMapper.toClientDoc(client)));
            } else if (client.getClass().equals(Staff.class)) {
                this.updateStaff(StaffMapper.toStaff(ClientMapper.toClientDoc(client)));
            } else {
                this.updateClient(client);
            }
        } catch (ClientRepositoryException exception) {
            throw new ClientDeactivationException("Client object representation for given client object could not be found in the database.");
        }
    }

    private List<Client> getClients(List<Bson> clientFilters) throws ClientDocNullReferenceException {
        List<Client> listOfFoundClients = new ArrayList<>();
        for (ClientDoc clientDoc : getClientCollection().aggregate(clientFilters)) {
            listOfFoundClients.add(super.findClient(clientDoc.getClientID()));
        }
        return listOfFoundClients;
    }
}
