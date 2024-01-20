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
import pl.pas.gr3.cinema.consts.model.TicketConstants;
import pl.pas.gr3.cinema.consts.model.UserConstants;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.other.UserNullReferenceException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.user.*;
import pl.pas.gr3.cinema.exceptions.repositories.other.InvalidUUIDException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.UserActivationException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.UserDeactivationException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.UserTypeNotFoundException;
import pl.pas.gr3.cinema.messages.repositories.MongoRepositoryMessages;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.model.users.User;
import pl.pas.gr3.cinema.repositories.interfaces.UserRepositoryInterface;
import pl.pas.gr3.cinema.user_mappers.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository extends MongoRepository implements UserRepositoryInterface {

    private final String databaseName;
    private final static Logger logger = LoggerFactory.getLogger(UserRepository.class);
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
            Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "user_login", "user_password", "user_status_active"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the user object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "user_login": {
                                            "description": "String containing users login to the cinema web app.",
                                            "bsonType": "string",
                                            "minLength": 8,
                                            "maxLength": 20,
                                            "pattern": "^[^\s]*$"
                                        }
                                        "user_password": {
                                            "description": "String containing users password to the cinema web app.",
                                            "bsonType": "string",
                                            "minLength": 8,
                                            "maxLength": 40,
                                            "pattern": "^[^\s]*$"
                                        }
                                        "user_status_active": {
                                            "description": "Boolean flag indicating whether user is able to perform any action.",
                                            "bsonType": "bool"
                                        }
                                    }
                                }
                            }
                            """));

    public UserRepository() {
        this.databaseName = "default";
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(userCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(userCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(userCollectionName, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(userCollectionName).createIndex(Indexes.ascending(UserConstants.USER_LOGIN), indexOptions);
        }
    }

    @PostConstruct
    public void initializeDatabaseState() {
        UUID clientIDNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIDNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        UUID clientIDNo3 = UUID.fromString("30392328-2cae-4e76-abb8-b1aa8f58a9e4");

        UUID adminIDNo1 = UUID.fromString("17dad3c7-7605-4808-bec5-d6f46abd23b8");
        UUID adminIDNo2 = UUID.fromString("ca857499-cdd5-4de3-a8d2-1ba7afcec2ef");
        UUID adminIDNo3 = UUID.fromString("07f97385-a2a3-474e-af61-f53d14a64198");

        UUID staffIDNo1 = UUID.fromString("67a85b0f-d063-4c9b-b223-fcc606c00f2f");
        UUID staffIDNo2 = UUID.fromString("3d8ef63c-f99d-445c-85d0-4b14e68fc5a1");
        UUID staffIDNo3 = UUID.fromString("86e394dd-e192-4390-b4e4-76029c879857");

        Client clientNo1 = new Client(clientIDNo1, "NewClientLogin1", "password");
        Client clientNo2 = new Client(clientIDNo2, "NewClientLogin2", "password");
        Client clientNo3 = new Client(clientIDNo3, "NewClientLogin3", "password");

        Admin adminNo1 = new Admin(adminIDNo1, "NewAdminLogin1", "password");
        Admin adminNo2 = new Admin(adminIDNo2, "NewAdminLogin2", "password");
        Admin adminNo3 = new Admin(adminIDNo3, "NewAdminLogin3", "password");

        Staff staffNo1 = new Staff(staffIDNo1, "NewStaffLogin1", "password");
        Staff staffNo2 = new Staff(staffIDNo2, "NewStaffLogin2", "password");
        Staff staffNo3 = new Staff(staffIDNo3, "NewStaffLogin3", "password");

        List<User> listOfClients = List.of(clientNo1, clientNo2, clientNo3, adminNo1, adminNo2, adminNo3, staffNo1, staffNo2, staffNo3);
        for (User user : listOfClients) {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, user.getUserID());
            if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(Client.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(Admin.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(Staff.class)) {
                this.getClientCollection().insertOne(user);
            }
        }
    }

    @PreDestroy
    public void restoreDatabaseState() {
        try {
            List<Client> listOfClients = this.findAllClients();
            for (Client client : listOfClients) {
                this.delete(client.getUserID(), UserConstants.CLIENT_DISCRIMINATOR);
            }
            List<Admin> listOfAdmins = this.findAllAdmins();
            for (Admin admin : listOfAdmins) {
                this.delete(admin.getUserID(), UserConstants.ADMIN_DISCRIMINATOR);
            }
            List<Staff> listOfStaffs = this.findAllStaffs();
            for (Staff staff : listOfStaffs) {
                this.delete(staff.getUserID(), UserConstants.STAFF_DISCRIMINATOR);
            }
        } catch (UserRepositoryException exception) {
            logger.debug(exception.getMessage());
        }
        this.close();
    }

    public UserRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(userCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(userCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(userCollectionName, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(userCollectionName).createIndex(Indexes.ascending(UserConstants.USER_LOGIN), indexOptions);
        }
    }

    // Create methods

    @Override
    public Client createClient(String clientLogin, String clientPassword) throws UserRepositoryException {
        Client client;
        try {
            client = new Client(UUID.randomUUID(), clientLogin, clientPassword);
            getClientCollection().insertOne(client);
        } catch (MongoWriteException exception) {
            if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                throw new UserRepositoryCreateUserDuplicateLoginException("Client could not be created, since login is already used by other user.");
            } else {
                throw new UserRepositoryCreateException(exception.getMessage(), exception);
            }

        }
        return client;
    }

    @Override
    public Admin createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException {
        Admin admin;
        try {
            admin = new Admin(UUID.randomUUID(), adminLogin, adminPassword);
            getClientCollection().insertOne(admin);
        } catch (MongoWriteException exception) {
            if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                throw new UserRepositoryCreateUserDuplicateLoginException("Admin could not be created, since login is already used by other user.");
            } else {
                throw new UserRepositoryCreateException(exception.getMessage(), exception);
            }
        }
        return admin;
    }

    @Override
    public Staff createStaff(String clientLogin, String clientPassword) throws UserRepositoryException {
        Staff staff;
        try {
            staff = new Staff(UUID.randomUUID(), clientLogin, clientPassword);
            getClientCollection().insertOne(staff);
        } catch (MongoWriteException exception) {
            if (exception.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                throw new UserRepositoryCreateUserDuplicateLoginException("Staff could not be created, since login is already used by other user.");
            } else {
                throw new UserRepositoryCreateException(exception.getMessage(), exception);
            }
        }
        return staff;
    }

    // Read methods

    @Override
    public User findByUUID(UUID clientID) throws UserRepositoryReadException {
        User client;
        try {
            client = super.findUser(clientID);
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    public User findByLogin(String userLogin) throws UserRepositoryReadException {
        User user;
        try {
            Bson filter = Filters.eq(UserConstants.USER_LOGIN, userLogin);
            user = getClientCollection().find(filter).first();
            if (user != null) {
                return user;
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.USER_DOC_OBJECT_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
    }

    // Find users by UUID

    public Client findClientByUUID(UUID clientID) throws UserRepositoryReadException {
        Client client;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, clientID)));
            User foundClientUser = getClientCollection().aggregate(aggregate).first();
            if (foundClientUser != null) {
                client = UserMapper.toClient(foundClientUser);
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            }
        } catch (UserNullReferenceException exception) {
            throw new UserRepositoryUserNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    public Admin findAdminByUUID(UUID adminID) throws UserRepositoryReadException {
        Admin admin;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, adminID)));
            User foundAdminUser = getClientCollection().aggregate(aggregate).first();
            if (foundAdminUser != null) {
                admin = UserMapper.toAdmin(foundAdminUser);
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_OBJECT_NOT_FOUND);
            }
        } catch (UserNullReferenceException exception) {
            throw new UserRepositoryAdminNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return admin;
    }

    public Staff findStaffByUUID(UUID staffID) throws UserRepositoryReadException {
        Staff staff;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, staffID)));
            User foundStaffUser = getClientCollection().aggregate(aggregate).first();
            if (foundStaffUser != null) {
                staff = UserMapper.toStaff(foundStaffUser);
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.STAFF_DOC_OBJECT_NOT_FOUND);
            }
        } catch (UserNullReferenceException exception) {
            throw new UserRepositoryStaffNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return staff;
    }

    // Find all users methods

    @Override
    public List<Client> findAllClients() throws UserRepositoryException {
        List<Client> listOfAllClients = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR);
            List<User> listOfClientUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (User clientUser : listOfClientUsers) {
                listOfAllClients.add(UserMapper.toClient(clientUser));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllClients;
    }

    @Override
    public List<Admin> findAllAdmins() throws UserRepositoryException {
        List<Admin> listOfAllAdmins = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR);
            List<User> listOfAdminUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (User adminUser : listOfAdminUsers) {
                listOfAllAdmins.add(UserMapper.toAdmin(adminUser));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllAdmins;
    }

    @Override
    public List<Staff> findAllStaffs() throws UserRepositoryException {
        List<Staff> listOfAllStaff = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR);
            List<User> listOfStaffUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (User staffUser : listOfStaffUsers) {
                listOfAllStaff.add(UserMapper.toStaff(staffUser));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllStaff;
    }

    // Find users by login

    @Override
    public Client findClientByLogin(String loginValue) throws UserRepositoryException {
        Client client;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, loginValue)));
            User foundClientUser = getClientCollection().aggregate(listOfFilters).first();
            if (foundClientUser != null) {
                client = UserMapper.toClient(foundClientUser);
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            }
        } catch (UserNullReferenceException exception) {
            throw new UserRepositoryUserNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return client;
    }

    @Override
    public Admin findAdminByLogin(String loginValue) throws UserRepositoryException {
        Admin admin;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, loginValue)));
            User foundAdminUser = getClientCollection().aggregate(listOfFilters).first();
            if (foundAdminUser != null) {
                admin = UserMapper.toAdmin(foundAdminUser);
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_OBJECT_NOT_FOUND);
            }
        } catch (UserNullReferenceException exception) {
            throw new UserRepositoryAdminNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return admin;
    }

    @Override
    public Staff findStaffByLogin(String loginValue) throws UserRepositoryException {
        Staff staff;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, loginValue)));
            User foundStaffUser = getClientCollection().aggregate(listOfFilters).first();
            if (foundStaffUser != null) {
                staff = UserMapper.toStaff(foundStaffUser);
            } else {
                throw new UserNullReferenceException(MongoRepositoryMessages.STAFF_DOC_OBJECT_NOT_FOUND);
            }
        } catch (UserNullReferenceException exception) {
            throw new UserRepositoryStaffNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return staff;
    }

    // Find all users matching login

    @Override
    public List<Client> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<Client> listOfMatchingClients = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (User user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingClients.add(UserMapper.toClient(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingClients;
    }

    @Override
    public List<Admin> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<Admin> listOfMatchingAdmins = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (User user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingAdmins.add(UserMapper.toAdmin(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingAdmins;
    }

    @Override
    public List<Staff> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<Staff> listOfMatchingStaff = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (User user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingStaff.add(UserMapper.toStaff(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingStaff;
    }

    // Other read methods

    public List<Ticket> getListOfTicketsForClient(UUID clientID, String name) throws UserRepositoryReadException {
        try {
            List<Ticket> listOfActiveTickets;
            Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, clientID);
            Document user = getClientCollectionWithoutType().find(clientFilter).first();
            if (user == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.DOC_OBJECT_NOT_FOUND);
            } else if (user.getString(UserConstants.USER_DISCRIMINATOR_NAME).equals(name)) {
                List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(TicketConstants.USER_ID, clientID)));
                listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
                return listOfActiveTickets;
            } else {
                throw new InvalidUUIDException(MongoRepositoryMessages.ID_REFERENCE_TO_DOCUMENT_OF_DIFFERENT_TYPE);
            }
        } catch (UserNullReferenceException | InvalidUUIDException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
    }

    // Update users methods

    @Override
    public void updateClient(Client client) throws UserRepositoryException {
        try {
            Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, client.getUserID());
            User updatedClientUser = getClientCollection().findOneAndReplace(clientFilter, client);
            if (updatedClientUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateAdmin(Admin admin) throws UserRepositoryException {
        try {
            Bson adminFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, admin.getUserID());
            User updatedAdminUser = getClientCollection().findOneAndReplace(adminFilter, admin);
            if (updatedAdminUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_FOR_ADMIN_OBJ_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateStaff(Staff staff) throws UserRepositoryException {
        try {
            Bson staffFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, staff.getUserID());
            User updatedStaffUser = getClientCollection().findOneAndReplace(staffFilter, staff);
            if (updatedStaffUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.STAFF_DOC_FOR_STAFF_OBJ_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    public void delete(UUID userID, String type) throws UserRepositoryException {
        try {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, userID);
            Document clientDoc = getClientCollectionWithoutType().find(filter).first();
            if (clientDoc == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.DOC_OBJECT_NOT_FOUND);
            } else if (clientDoc.getString(UserConstants.USER_DISCRIMINATOR_NAME).equals(type)) {
                getClientCollection().findOneAndDelete(filter);
            } else {
                throw new InvalidUUIDException(MongoRepositoryMessages.ID_REFERENCE_TO_DOCUMENT_OF_DIFFERENT_TYPE);
            }
        } catch (MongoException | InvalidUUIDException | UserNullReferenceException exception) {
            throw new UserRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(User user, String name) throws UserRepositoryException {
        user.setUserStatusActive(true);
        try {
            this.updateCertainUserBasedOnType(user, name);
        } catch (UserRepositoryException exception) {
            throw new UserActivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    @Override
    public void deactivate(User user, String name) throws UserRepositoryException {
        user.setUserStatusActive(false);
        try {
            this.updateCertainUserBasedOnType(user, name);
        } catch (UserRepositoryException exception) {
            throw new UserDeactivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    private void updateCertainUserBasedOnType(User user, String name) throws UserRepositoryException {
        try {
            switch (name) {
                case UserConstants.ADMIN_DISCRIMINATOR -> this.updateAdmin(UserMapper.toAdmin(user));
                case UserConstants.STAFF_DISCRIMINATOR -> this.updateStaff(UserMapper.toStaff(user));
                case UserConstants.CLIENT_DISCRIMINATOR -> this.updateClient(UserMapper.toClient(user));
                default -> throw new UserTypeNotFoundException(MongoRepositoryMessages.USER_TYPE_NOT_FOUND);
            }
        } catch (UserRepositoryException | UserTypeNotFoundException exception) {
            throw new UserActivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }
}
