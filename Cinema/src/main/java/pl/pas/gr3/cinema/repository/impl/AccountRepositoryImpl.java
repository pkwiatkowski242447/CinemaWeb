package pl.pas.gr3.cinema.repository.impl;

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
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.exception.bad_request.AccountActivationException;
import pl.pas.gr3.cinema.exception.bad_request.AccountCreateException;
import pl.pas.gr3.cinema.exception.bad_request.AccountDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.AccountUpdateException;
import pl.pas.gr3.cinema.exception.conflict.LoginAlreadyTakenException;
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.ClientNotFoundException;
import pl.pas.gr3.cinema.util.consts.model.TicketConstants;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.api.UserRepository;
import pl.pas.gr3.cinema.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountRepositoryImpl extends MongoRepository implements UserRepository {

    private final String databaseName;
    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryImpl.class);
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
        Document.parse("""
            {
                $jsonSchema: {
                    "bsonType": "object",
                    "required": ["_id", "user_login", "user_password", "user_status_active"],
                    "properties": {
                        "_id": {
                            "description": "Id of the account object representation in the database.",
                            "bsonType": "binData",
                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                        }
                        "user_login": {
                            "description": "String containing users login to the cinema web app.",
                            "bsonType": "string",
                            "minLength": 8,
                            "maxLength": 20,
                            "pattern": "^[^\\s]*$"
                        }
                        "user_password": {
                            "description": "String containing users password to the cinema web app.",
                            "bsonType": "string",
                            "minLength": 8,
                            "maxLength": 200,
                            "pattern": "^[^\\s]*$"
                        }
                        "user_status_active": {
                            "description": "Boolean flag indicating whether account is able to perform any action.",
                            "bsonType": "bool"
                        }
                    }
                }
            }
            """));

    public AccountRepositoryImpl() {
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
        UUID clientIdNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIdNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        UUID clientIdNo3 = UUID.fromString("30392328-2cae-4e76-abb8-b1aa8f58a9e4");

        UUID adminIdNo1 = UUID.fromString("17dad3c7-7605-4808-bec5-d6f46abd23b8");
        UUID adminIdNo2 = UUID.fromString("ca857499-cdd5-4de3-a8d2-1ba7afcec2ef");
        UUID adminIdNo3 = UUID.fromString("07f97385-a2a3-474e-af61-f53d14a64198");

        UUID staffIdNo1 = UUID.fromString("67a85b0f-d063-4c9b-b223-fcc606c00f2f");
        UUID staffIdNo2 = UUID.fromString("3d8ef63c-f99d-445c-85d0-4b14e68fc5a1");
        UUID staffIdNo3 = UUID.fromString("86e394dd-e192-4390-b4e4-76029c879857");

        Client clientNo1 = new Client(clientIdNo1, "NewClientLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Client clientNo2 = new Client(clientIdNo2, "NewClientLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Client clientNo3 = new Client(clientIdNo3, "NewClientLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        Admin adminNo1 = new Admin(adminIdNo1, "NewAdminLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Admin adminNo2 = new Admin(adminIdNo2, "NewAdminLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Admin adminNo3 = new Admin(adminIdNo3, "NewAdminLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        Staff staffNo1 = new Staff(staffIdNo1, "NewStaffLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Staff staffNo2 = new Staff(staffIdNo2, "NewStaffLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        Staff staffNo3 = new Staff(staffIdNo3, "NewStaffLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        List<Account> accounts = List.of(clientNo1, clientNo2, clientNo3, adminNo1, adminNo2, adminNo3, staffNo1, staffNo2, staffNo3);
        for (Account account : accounts) {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, account.getId());
            if (getClientCollection().find(filter).first() == null &&
                account.getClass().equals(Client.class)) getClientCollection().insertOne(account);

            else if (getClientCollection().find(filter).first() == null &&
                account.getClass().equals(Admin.class)) this.getClientCollection().insertOne(account);

            else if (getClientCollection().find(filter).first() == null &&
                account.getClass().equals(Staff.class)) this.getClientCollection().insertOne(account);
        }
    }

    @PreDestroy
    public void restoreDatabaseState() {
        try {
            List<Client> clients = findAllClients();
            clients.forEach(client -> delete(client.getId(), UserConstants.CLIENT_DISCRIMINATOR));

            List<Admin> admins = findAllAdmins();
            admins.forEach(admin -> delete(admin.getId(), UserConstants.ADMIN_DISCRIMINATOR));

            List<Staff> listOfStaffs = findAllStaffs();
            listOfStaffs.forEach(staff -> delete(staff.getId(), UserConstants.STAFF_DISCRIMINATOR));
        } catch (Exception exception) {
            logger.debug(exception.getMessage());
        }

        close();
    }

    public AccountRepositoryImpl(String databaseName) {
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

    /* CREATE */

    @Override
    public Client createClient(String login, String password) {
        try {
            Client client = new Client(UUID.randomUUID(), login, password);
            getClientCollection().insertOne(client);
            return client;
        } catch (MongoWriteException exception) {
            ErrorCategory category = exception.getError().getCategory();
            if (category.equals(ErrorCategory.DUPLICATE_KEY))
                throw new LoginAlreadyTakenException(exception);
            else throw new AccountCreateException(exception);
        }
    }

    @Override
    public Admin createAdmin(String login, String password) {
        try {
            Admin admin = new Admin(UUID.randomUUID(), login, password);
            getClientCollection().insertOne(admin);
            return admin;
        } catch (MongoWriteException exception) {
            ErrorCategory category = exception.getError().getCategory();
            if (category.equals(ErrorCategory.DUPLICATE_KEY)) throw new LoginAlreadyTakenException(exception);
            else throw new AccountCreateException(exception);
        }
    }

    @Override
    public Staff createStaff(String login, String password) {
        try {
            Staff staff = new Staff(UUID.randomUUID(), login, password);
            getClientCollection().insertOne(staff);
            return staff;
        } catch (MongoWriteException exception) {
            ErrorCategory category = exception.getError().getCategory();
            if (category.equals(ErrorCategory.DUPLICATE_KEY))
                throw new LoginAlreadyTakenException(exception);
            else throw new AccountCreateException(exception);
        }
    }

    /* READ */

    @Override
    public Account findByUUID(UUID clientID) {
        try {
            return super.findUser(clientID);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    public Account findByLogin(String userLogin) {
        try {
            Bson filter = Filters.eq(UserConstants.USER_LOGIN, userLogin);
            Account account = getClientCollection().find(filter).first();
            if (account == null) throw new AccountNotFoundException();
            return account;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    public Client findClientByUUID(UUID clientId) {
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, clientId)));

            Account foundClient = getClientCollection().aggregate(aggregate).first();
            if (foundClient == null) throw new AccountNotFoundException();
            return UserMapper.toClient(foundClient);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    public Admin findAdminByUUID(UUID adminId) {
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, adminId)));

            Account foundAdmin = getClientCollection().aggregate(aggregate).first();
            if (foundAdmin == null) throw new AccountNotFoundException();
            return UserMapper.toAdmin(foundAdmin);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    public Staff findStaffByUUID(UUID staffId) {
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, staffId)));

            Account foundStaffAccount = getClientCollection().aggregate(aggregate).first();
            if (foundStaffAccount == null) throw new AccountNotFoundException();
            return UserMapper.toStaff(foundStaffAccount);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public List<Client> findAllClients() {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR);
            List<Account> accountClients = getClientCollection().find(filter).into(new ArrayList<>());
            List<Client> clients = accountClients.stream().map(UserMapper::toClient).toList();
            clientSession.commitTransaction();
            return clients;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public List<Admin> findAllAdmins() {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR);
            List<Account> accountAdmins = getClientCollection().find(filter).into(new ArrayList<>());
            List<Admin> admins = accountAdmins.stream().map(UserMapper::toAdmin).toList();
            clientSession.commitTransaction();
            return admins;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public List<Staff> findAllStaffs() {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR);
            List<Account> accountStaffs = getClientCollection().find(filter).into(new ArrayList<>());
            List<Staff> staffs = accountStaffs.stream().map(UserMapper::toStaff).toList();
            clientSession.commitTransaction();
            return staffs;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public Client findClientByLogin(String login) {
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, login)));

            Account foundClient = getClientCollection().aggregate(listOfFilters).first();
            if (foundClient == null) throw new AccountNotFoundException();
            return UserMapper.toClient(foundClient);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public Admin findAdminByLogin(String login) {
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, login)));

            Account foundAdminAccount = getClientCollection().aggregate(listOfFilters).first();
            if (foundAdminAccount == null) throw new AccountNotFoundException();
            return UserMapper.toAdmin(foundAdminAccount);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public Staff findStaffByLogin(String login) {
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, login)));

            Account foundStaffAccount = getClientCollection().aggregate(listOfFilters).first();
            if (foundStaffAccount == null) throw new AccountNotFoundException();
            return UserMapper.toStaff(foundStaffAccount);
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public List<Client> findAllClientsMatchingLogin(String login) {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();

            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + login + ".*$")));

            List<Client> clients = getClientCollection().aggregate(listOfFilters).into(new ArrayList<>())
                .stream().map(UserMapper::toClient).toList();

            clientSession.commitTransaction();
            return clients;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public List<Admin> findAllAdminsMatchingLogin(String login) {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();

            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + login + ".*$")));

            List<Admin> admins = getClientCollection().aggregate(listOfFilters).into(new ArrayList<>())
                .stream().map(UserMapper::toAdmin).toList();

            clientSession.commitTransaction();
            return admins;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    @Override
    public List<Staff> findAllStaffsMatchingLogin(String login) {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();

            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + login + ".*$")));

            List<Staff> staffs = getClientCollection().aggregate(listOfFilters).into(new ArrayList<>())
                .stream().map(UserMapper::toStaff).toList();

            clientSession.commitTransaction();
            return staffs;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    public List<Ticket> getListOfTicketsForClient(UUID userId, String name) {
        Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, userId);
        Document account = getClientCollectionWithoutType().find(clientFilter).first();
        if (account == null) throw new ClientNotFoundException();
        else if (account.getString(UserConstants.USER_DISCRIMINATOR_NAME).equals(name)) {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(TicketConstants.USER_ID, userId)));
            return findTicketsWithAggregate(listOfFilters);
        }
        return new ArrayList<>();
    }

    /* UPDATE */

    @Override
    public void updateClient(Client client) {
        try {
            Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, client.getId());
            Account updatedClientAccount = getClientCollection().findOneAndReplace(clientFilter, client);
            if (updatedClientAccount == null) throw new AccountNotFoundException();
        } catch (MongoException exception) {
            throw new AccountUpdateException(exception);
        }
    }

    @Override
    public void updateAdmin(Admin admin) {
        try {
            Bson adminFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, admin.getId());
            Account updatedAdminAccount = getClientCollection().findOneAndReplace(adminFilter, admin);
            if (updatedAdminAccount == null) throw new AccountNotFoundException();
        } catch (MongoException exception) {
            throw new AccountUpdateException(exception);
        }
    }

    @Override
    public void updateStaff(Staff staff) {
        try {
            Bson staffFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, staff.getId());
            Account updatedStaffAccount = getClientCollection().findOneAndReplace(staffFilter, staff);
            if (updatedStaffAccount == null) throw new AccountNotFoundException();
        } catch (MongoException exception) {
            throw new AccountUpdateException(exception);
        }
    }

    /* DELETE */

    public void delete(UUID userId, String type) {
        try {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, userId);
            Document clientDoc = getClientCollectionWithoutType().find(filter).first();
            if (clientDoc == null) throw new AccountNotFoundException();
            else if (clientDoc.getString(UserConstants.USER_DISCRIMINATOR_NAME).equals(type)) {
                getClientCollection().findOneAndDelete(filter);
            } else throw new AccountDeleteException();
        } catch (MongoException exception) {
            throw new AccountDeleteException(exception);
        }
    }

    @Override
    public void activate(Account account, String name) {
        account.setActive(true);
        updateCertainUserBasedOnType(account, name);
    }

    @Override
    public void deactivate(Account account, String name) {
        account.setActive(false);
        updateCertainUserBasedOnType(account, name);
    }

    /* OTHER */

    private void updateCertainUserBasedOnType(Account account, String name) {
        switch (name) {
            case UserConstants.ADMIN_DISCRIMINATOR -> updateAdmin(UserMapper.toAdmin(account));
            case UserConstants.STAFF_DISCRIMINATOR -> updateStaff(UserMapper.toStaff(account));
            case UserConstants.CLIENT_DISCRIMINATOR -> updateClient(UserMapper.toClient(account));
            default -> throw new AccountActivationException();
        }
    }
}
