package pl.pas.gr3.cinema.repository.impl;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import pl.pas.gr3.cinema.repository.api.AccountRepository;
import pl.pas.gr3.cinema.mapper.AccountMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountRepositoryImpl extends MongoRepository implements AccountRepository {

    @Autowired
    private AccountMapper accountMapper;

    private final String databaseName;
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
                            "pattern": "^[^\s]*$"
                        }
                        "user_password": {
                            "description": "String containing users password to the cinema web app.",
                            "bsonType": "string",
                            "minLength": 8,
                            "maxLength": 200,
                            "pattern": "^[^\s]*$"
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

        mongoDatabase.getCollection(USERS_COLLECTION_NAME).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(USERS_COLLECTION_NAME)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(USERS_COLLECTION_NAME, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(USERS_COLLECTION_NAME).createIndex(Indexes.ascending(UserConstants.USER_LOGIN), indexOptions);
        }
    }

    public AccountRepositoryImpl(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(USERS_COLLECTION_NAME).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(USERS_COLLECTION_NAME)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
            mongoDatabase.createCollection(USERS_COLLECTION_NAME, createCollectionOptions);
            IndexOptions indexOptions = new IndexOptions().unique(true);
            mongoDatabase.getCollection(USERS_COLLECTION_NAME).createIndex(Indexes.ascending(UserConstants.USER_LOGIN), indexOptions);
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
            return accountMapper.toClient(foundClient);
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
            return accountMapper.toAdmin(foundAdmin);
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
            return accountMapper.toStaff(foundStaffAccount);
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
            List<Client> clients = accountClients.stream().map(accountMapper::toClient).toList();
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
            List<Admin> admins = accountAdmins.stream().map(accountMapper::toAdmin).toList();
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
            List<Staff> staffs = accountStaffs.stream().map(accountMapper::toStaff).toList();
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
            return accountMapper.toClient(foundClient);
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
            return accountMapper.toAdmin(foundAdminAccount);
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
            return accountMapper.toStaff(foundStaffAccount);
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
                .stream().map(accountMapper::toClient).toList();

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
                .stream().map(accountMapper::toAdmin).toList();

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
                .stream().map(accountMapper::toStaff).toList();

            clientSession.commitTransaction();
            return staffs;
        } catch (MongoException exception) {
            throw new AccountNotFoundException(exception);
        }
    }

    public List<Ticket> getListOfTicketsForClient(UUID userId, String name) {
        Bson clientFilter = Filters.and(
            Filters.eq(UserConstants.GENERAL_IDENTIFIER, userId),
            Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, name)
        );

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
            case UserConstants.ADMIN_DISCRIMINATOR -> updateAdmin(accountMapper.toAdmin(account));
            case UserConstants.STAFF_DISCRIMINATOR -> updateStaff(accountMapper.toStaff(account));
            case UserConstants.CLIENT_DISCRIMINATOR -> updateClient(accountMapper.toClient(account));
            default -> throw new AccountActivationException();
        }
    }
}
