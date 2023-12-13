package pl.pas.gr3.cinema.repositories.implementations;

import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.mapping.ClientDocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.mapping.DocNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.*;
import pl.pas.gr3.cinema.exceptions.repositories.other.InvalidUUIDException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientActivationException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientDeactivationException;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;
import pl.pas.gr3.cinema.mapping.mappers.users.AdminMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.ClientMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.StaffMapper;
import pl.pas.gr3.cinema.messages.repositories.MongoRepositoryMessages;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.interfaces.ClientRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ClientRepository extends MongoRepository implements ClientRepositoryInterface {

    private final String databaseName;
    private final static Logger logger = LoggerFactory.getLogger(ClientRepository.class);
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

        mongoDatabase.getCollection(clientCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(clientCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(clientCollectionName, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(clientCollectionName).createIndex(Indexes.ascending(MongoRepositoryConstants.CLIENT_LOGIN), indexOptions);
        }
    }

    @PostConstruct
    public void initializeDatabaseState() {
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
    }

    @PreDestroy
    public void restoreDatabaseState() {
        try {
            List<Client> listOfClients = this.findAllClients();
            for (Client client : listOfClients) {
                this.delete(client.getClientID(), MongoRepositoryConstants.CLIENT_SUBCLASS);
            }
            List<Admin> listOfAdmins = this.findAllAdmins();
            for (Admin admin : listOfAdmins) {
                this.delete(admin.getClientID(), MongoRepositoryConstants.ADMIN_SUBCLASS);
            }
            List<Staff> listOfStaffs = this.findAllStaffs();
            for (Staff staff : listOfStaffs) {
                this.delete(staff.getClientID(), MongoRepositoryConstants.STAFF_SUBCLASS);
            }
        } catch (ClientRepositoryException exception) {
            logger.debug(exception.getMessage());
        }
        this.close();
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
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(clientCollectionName, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(clientCollectionName).createIndex(Indexes.ascending(MongoRepositoryConstants.CLIENT_LOGIN), indexOptions);
        }
    }

    // Create methods

    @Override
    public Client createClient(String clientLogin, String clientPassword) throws ClientRepositoryException {
        Client client;
        try {
            client = new Client(UUID.randomUUID(), clientLogin, clientPassword);
            ClientDoc clientDoc = ClientMapper.toClientDoc(client);
            getClientCollection().insertOne(clientDoc);
        } catch (MongoWriteException exception) {
            if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                throw new ClientRepositoryCreateClientDuplicateLoginException("Client could not be created, since login is already used by other user.");
            } else {
                throw new ClientRepositoryCreateException(exception.getMessage(), exception);
            }

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
        } catch (MongoWriteException exception) {
            if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                throw new ClientRepositoryCreateClientDuplicateLoginException("Admin could not be created, since login is already used by other user.");
            } else {
                throw new ClientRepositoryCreateException(exception.getMessage(), exception);
            }
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
        } catch (MongoWriteException exception) {
            if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                throw new ClientRepositoryCreateClientDuplicateLoginException("Staff could not be created, since login is already used by other user.");
            } else {
                throw new ClientRepositoryCreateException(exception.getMessage(), exception);
            }
        }
        return staff;
    }

    // Read methods

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

    // Find users by UUID

    public Client findClientByUUID(UUID clientID) throws ClientRepositoryReadException {
        Client client;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.CLIENT_SUBCLASS)),
                    Aggregates.match(Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, clientID)));
            ClientDoc foundClientDoc = getClientCollection().aggregate(aggregate).first();
            if (foundClientDoc != null) {
                client = ClientMapper.toClient(foundClientDoc);
            } else {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            }
        } catch (ClientDocNullReferenceException exception) {
            throw new ClientRepositoryClientNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    public Admin findAdminByUUID(UUID adminID) throws ClientRepositoryReadException {
        Admin admin;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.ADMIN_SUBCLASS)),
                    Aggregates.match(Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, adminID)));
            ClientDoc foundAdminDoc = getClientCollection().aggregate(aggregate).first();
            if (foundAdminDoc != null) {
                admin = AdminMapper.toAdmin(foundAdminDoc);
            } else {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_OBJECT_NOT_FOUND);
            }
        } catch (ClientDocNullReferenceException exception) {
            throw new ClientRepositoryAdminNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return admin;
    }

    public Staff findStaffByUUID(UUID staffID) throws ClientRepositoryReadException {
        Staff staff;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.STAFF_SUBCLASS)),
                    Aggregates.match(Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, staffID)));
            ClientDoc foundStaffDoc = getClientCollection().aggregate(aggregate).first();
            if (foundStaffDoc != null) {
                staff = StaffMapper.toStaff(foundStaffDoc);
            } else {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.STAFF_DOC_OBJECT_NOT_FOUND);
            }
        } catch (ClientDocNullReferenceException exception) {
            throw new ClientRepositoryStaffNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return staff;
    }

    // Find all users methods

    @Override
    public List<Client> findAllClients() throws ClientRepositoryException {
        List<Client> listOfAllClients = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.CLIENT_SUBCLASS);
            List<ClientDoc> listOfClientDocs = getClientCollection().find(filter).into(new ArrayList<>());
            for (ClientDoc clientDoc : listOfClientDocs) {
                listOfAllClients.add(ClientMapper.toClient(clientDoc));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllClients;
    }

    @Override
    public List<Admin> findAllAdmins() throws ClientRepositoryException {
        List<Admin> listOfAllAdmins = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.ADMIN_SUBCLASS);
            List<ClientDoc> listOfAdminsDocs = getClientCollection().find(filter).into(new ArrayList<>());
            for (ClientDoc adminDoc : listOfAdminsDocs) {
                listOfAllAdmins.add(AdminMapper.toAdmin(adminDoc));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllAdmins;
    }

    @Override
    public List<Staff> findAllStaffs() throws ClientRepositoryException {
        List<Staff> listOfAllStaff = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.STAFF_SUBCLASS);
            List<ClientDoc> listOfStaffDocs = getClientCollection().find(filter).into(new ArrayList<>());
            for (ClientDoc staffDoc : listOfStaffDocs) {
                listOfAllStaff.add(StaffMapper.toStaff(staffDoc));
            }
            clientSession.commitTransaction();
        } catch (MongoException | DocNullReferenceException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllStaff;
    }

    // Find users by login

    @Override
    public Client findClientByLogin(String loginValue) throws ClientRepositoryException {
        Client client;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.CLIENT_SUBCLASS)),
                    Aggregates.match(Filters.eq(MongoRepositoryConstants.CLIENT_LOGIN, loginValue)));
            ClientDoc foundClientDoc = getClientCollection().aggregate(listOfFilters).first();
            if (foundClientDoc != null) {
                client = ClientMapper.toClient(foundClientDoc);
            } else {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            }
        } catch (ClientDocNullReferenceException exception) {
            throw new ClientRepositoryClientNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    @Override
    public Admin findAdminByLogin(String loginValue) throws ClientRepositoryException {
        Admin admin;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.ADMIN_SUBCLASS)),
                    Aggregates.match(Filters.eq(MongoRepositoryConstants.CLIENT_LOGIN, loginValue)));
            ClientDoc foundAdminDoc = getClientCollection().aggregate(listOfFilters).first();
            if (foundAdminDoc != null) {
                admin = AdminMapper.toAdmin(foundAdminDoc);
            } else {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_OBJECT_NOT_FOUND);
            }
        } catch (ClientDocNullReferenceException exception) {
            throw new ClientRepositoryAdminNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return admin;
    }

    @Override
    public Staff findStaffByLogin(String loginValue) throws ClientRepositoryException {
        Staff staff;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.STAFF_SUBCLASS)),
                    Aggregates.match(Filters.eq(MongoRepositoryConstants.CLIENT_LOGIN, loginValue)));
            ClientDoc foundStaffDoc = getClientCollection().aggregate(listOfFilters).first();
            if (foundStaffDoc != null) {
                staff = StaffMapper.toStaff(foundStaffDoc);
            } else {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.STAFF_DOC_OBJECT_NOT_FOUND);
            }
        } catch (ClientDocNullReferenceException exception) {
            throw new ClientRepositoryStaffNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return staff;
    }

    // Find all users matching login

    @Override
    public List<Client> findAllClientsMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Client> listOfMatchingClients = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.CLIENT_SUBCLASS)),
                    Aggregates.match(Filters.regex(MongoRepositoryConstants.CLIENT_LOGIN, "^" + loginValue + ".*$")));
            for (ClientDoc clientDoc : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingClients.add(ClientMapper.toClient(clientDoc));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingClients;
    }

    @Override
    public List<Admin> findAllAdminsMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Admin> listOfMatchingAdmins = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.ADMIN_SUBCLASS)),
                    Aggregates.match(Filters.regex(MongoRepositoryConstants.CLIENT_LOGIN, "^" + loginValue + ".*$")));
            for (ClientDoc clientDoc : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingAdmins.add(AdminMapper.toAdmin(clientDoc));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingAdmins;
    }

    @Override
    public List<Staff> findAllStaffsMatchingLogin(String loginValue) throws ClientRepositoryException {
        List<Staff> listOfMatchingStaffs = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.USER_SUBCLASS, MongoRepositoryConstants.STAFF_SUBCLASS)),
                    Aggregates.match(Filters.regex(MongoRepositoryConstants.CLIENT_LOGIN, "^" + loginValue + ".*$")));
            for (ClientDoc clientDoc : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingStaffs.add(StaffMapper.toStaff(clientDoc));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingStaffs;
    }

    // Other read methods

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

    public List<Ticket> getListOfTicketsForClient(UUID clientID, String name) throws ClientRepositoryReadException {
        try {
            List<Ticket> listOfActiveTickets;
            Bson clientFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, clientID);
            Document clientDoc = getClientCollectionWithoutType().find(clientFilter).first();
            if (clientDoc == null) {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.DOC_OBJECT_NOT_FOUND);
            } else if (clientDoc.getString(MongoRepositoryConstants.USER_SUBCLASS).equals(name)) {
                List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(MongoRepositoryConstants.CLIENT_IDENTIFIER, clientID)));
                listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
                return listOfActiveTickets;
            } else {
                throw new InvalidUUIDException(MongoRepositoryMessages.ID_REFERENCE_TO_DOCUMENT_OF_DIFFERENT_TYPE);
            }
        } catch (ClientDocNullReferenceException | InvalidUUIDException exception) {
            throw new ClientRepositoryReadException(exception.getMessage(), exception);
        }
    }

    // Update users methods

    @Override
    public void updateClient(Client client) throws ClientRepositoryException {
        try {
            ClientDoc newClientDoc = ClientMapper.toClientDoc(client);
            Bson clientFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, client.getClientID());
            ClientDoc updatedClientDoc = getClientCollection().findOneAndReplace(clientFilter, newClientDoc);
            if (updatedClientDoc == null) {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateAdmin(Admin admin) throws ClientRepositoryException {
        try {
            ClientDoc newAdminDoc = AdminMapper.toAdminDoc(admin);
            Bson adminFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, admin.getClientID());
            ClientDoc updatedAdminDoc = getClientCollection().findOneAndReplace(adminFilter, newAdminDoc);
            if (updatedAdminDoc == null) {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_FOR_ADMIN_OBJ_NOT_FOUND);
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateStaff(Staff staff) throws ClientRepositoryException {
        try {
            ClientDoc newStaffDoc = StaffMapper.toStaffDoc(staff);
            Bson staffFilter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, staff.getClientID());
            ClientDoc updatedStaffDoc = getClientCollection().findOneAndReplace(staffFilter, newStaffDoc);
            if (updatedStaffDoc == null) {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.STAFF_DOC_FOR_STAFF_OBJ_NOT_FOUND);
            }
        } catch (MongoException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    public void delete(UUID userID, String type) throws ClientRepositoryException {
        try {
            Bson filter = Filters.eq(MongoRepositoryConstants.GENERAL_IDENTIFIER, userID);
            Document clientDoc = getClientCollectionWithoutType().find(filter).first();
            if (clientDoc == null) {
                throw new ClientDocNullReferenceException(MongoRepositoryMessages.DOC_OBJECT_NOT_FOUND);
            } else if (clientDoc.getString(MongoRepositoryConstants.USER_SUBCLASS).equals(type)) {
                getClientCollection().findOneAndDelete(filter);
            } else {
                throw new InvalidUUIDException(MongoRepositoryMessages.ID_REFERENCE_TO_DOCUMENT_OF_DIFFERENT_TYPE);
            }
        } catch (MongoException | InvalidUUIDException | ClientDocNullReferenceException exception) {
            throw new ClientRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(Client client, String name) throws ClientRepositoryException {
        client.setClientStatusActive(true);
        try {
            this.updateCertainUserBasedOnType(client, name);
        } catch (ClientRepositoryException exception) {
            throw new ClientActivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    @Override
    public void deactivate(Client client, String name) throws ClientRepositoryException {
        client.setClientStatusActive(false);
        try {
            this.updateCertainUserBasedOnType(client, name);
        } catch (ClientRepositoryException exception) {
            throw new ClientDeactivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    private void updateCertainUserBasedOnType(Client client, String name) throws ClientRepositoryException {
        try {
            switch (name) {
                case MongoRepositoryConstants.ADMIN_SUBCLASS: {
                    this.updateAdmin(AdminMapper.toAdmin(ClientMapper.toClientDoc(client)));
                    break;
                }
                case MongoRepositoryConstants.STAFF_SUBCLASS: {
                    this.updateStaff(StaffMapper.toStaff(ClientMapper.toClientDoc(client)));
                    break;
                }
                default: {
                    this.updateClient(client);
                }
            }
        } catch (ClientRepositoryException exception) {
            throw new ClientActivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }
}
