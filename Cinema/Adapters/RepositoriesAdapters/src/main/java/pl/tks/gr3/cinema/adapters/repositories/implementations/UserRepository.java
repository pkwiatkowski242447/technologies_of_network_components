package pl.tks.gr3.cinema.adapters.repositories.implementations;

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
import pl.tks.gr3.cinema.adapters.consts.model.TicketConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.UserNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.*;
import pl.tks.gr3.cinema.adapters.exceptions.other.InvalidUUIDException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserActivationException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserDeactivationException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserTypeNotFoundException;
import pl.tks.gr3.cinema.adapters.messages.repositories.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.adapters.repositories.interfaces.UserRepositoryInterface;
import pl.tks.gr3.cinema.adapters.user_mappers.UserMapper;

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
                                            "maxLength": 200,
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

        ClientEnt clientNo1 = new ClientEnt(clientIDNo1, "NewClientLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        ClientEnt clientNo2 = new ClientEnt(clientIDNo2, "NewClientLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        ClientEnt clientNo3 = new ClientEnt(clientIDNo3, "NewClientLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        AdminEnt adminNo1 = new AdminEnt(adminIDNo1, "NewAdminLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        AdminEnt adminNo2 = new AdminEnt(adminIDNo2, "NewAdminLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        AdminEnt adminNo3 = new AdminEnt(adminIDNo3, "NewAdminLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        StaffEnt staffNo1 = new StaffEnt(staffIDNo1, "NewStaffLogin1", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        StaffEnt staffNo2 = new StaffEnt(staffIDNo2, "NewStaffLogin2", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");
        StaffEnt staffNo3 = new StaffEnt(staffIDNo3, "NewStaffLogin3", "$2a$10$DbYLnx7YVVEtyJUOd7dFP.qUPAswrfNu6RVU0vB/Ti8us8AqaoKzS");

        List<UserEnt> listOfClients = List.of(clientNo1, clientNo2, clientNo3, adminNo1, adminNo2, adminNo3, staffNo1, staffNo2, staffNo3);
        for (UserEnt user : listOfClients) {
            Bson filter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, user.getUserID());
            if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(ClientEnt.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(AdminEnt.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(StaffEnt.class)) {
                this.getClientCollection().insertOne(user);
            }
        }
    }

    @PreDestroy
    public void restoreDatabaseState() {
        try {
            List<ClientEnt> listOfClients = this.findAllClients();
            for (ClientEnt client : listOfClients) {
                this.delete(client.getUserID(), UserConstants.CLIENT_DISCRIMINATOR);
            }
            List<AdminEnt> listOfAdmins = this.findAllAdmins();
            for (AdminEnt admin : listOfAdmins) {
                this.delete(admin.getUserID(), UserConstants.ADMIN_DISCRIMINATOR);
            }
            List<StaffEnt> listOfStaffs = this.findAllStaffs();
            for (StaffEnt staff : listOfStaffs) {
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
    public ClientEnt createClient(String clientLogin, String clientPassword) throws UserRepositoryException {
        ClientEnt client;
        try {
            client = new ClientEnt(UUID.randomUUID(), clientLogin, clientPassword);
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
    public AdminEnt createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException {
        AdminEnt admin;
        try {
            admin = new AdminEnt(UUID.randomUUID(), adminLogin, adminPassword);
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
    public StaffEnt createStaff(String clientLogin, String clientPassword) throws UserRepositoryException {
        StaffEnt staff;
        try {
            staff = new StaffEnt(UUID.randomUUID(), clientLogin, clientPassword);
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
    public UserEnt findByUUID(UUID clientID) throws UserRepositoryReadException {
        UserEnt user;
        try {
            user = super.findUser(clientID);
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return user;
    }

    public UserEnt findByLogin(String userLogin) throws UserRepositoryReadException {
        UserEnt user;
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

    public ClientEnt findClientByUUID(UUID clientID) throws UserRepositoryReadException {
        ClientEnt client;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, clientID)));
            UserEnt foundClientUser = getClientCollection().aggregate(aggregate).first();
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

    public AdminEnt findAdminByUUID(UUID adminID) throws UserRepositoryReadException {
        AdminEnt admin;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, adminID)));
            UserEnt foundAdminUser = getClientCollection().aggregate(aggregate).first();
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

    public StaffEnt findStaffByUUID(UUID staffID) throws UserRepositoryReadException {
        StaffEnt staff;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.GENERAL_IDENTIFIER, staffID)));
            UserEnt foundStaffUser = getClientCollection().aggregate(aggregate).first();
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
    public List<ClientEnt> findAllClients() throws UserRepositoryException {
        List<ClientEnt> listOfAllClients = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR);
            List<UserEnt> listOfClientUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (UserEnt clientUser : listOfClientUsers) {
                listOfAllClients.add(UserMapper.toClient(clientUser));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllClients;
    }

    @Override
    public List<AdminEnt> findAllAdmins() throws UserRepositoryException {
        List<AdminEnt> listOfAllAdmins = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR);
            List<UserEnt> listOfAdminUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (UserEnt adminUser : listOfAdminUsers) {
                listOfAllAdmins.add(UserMapper.toAdmin(adminUser));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllAdmins;
    }

    @Override
    public List<StaffEnt> findAllStaffs() throws UserRepositoryException {
        List<StaffEnt> listOfAllStaff = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR);
            List<UserEnt> listOfStaffUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (UserEnt staffUser : listOfStaffUsers) {
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
    public ClientEnt findClientByLogin(String loginValue) throws UserRepositoryException {
        ClientEnt client;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, loginValue)));
            UserEnt foundClientUser = getClientCollection().aggregate(listOfFilters).first();
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
    public AdminEnt findAdminByLogin(String loginValue) throws UserRepositoryException {
        AdminEnt admin;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, loginValue)));
            UserEnt foundAdminUser = getClientCollection().aggregate(listOfFilters).first();
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
    public StaffEnt findStaffByLogin(String loginValue) throws UserRepositoryException {
        StaffEnt staff;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserConstants.USER_LOGIN, loginValue)));
            UserEnt foundStaffUser = getClientCollection().aggregate(listOfFilters).first();
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
    public List<ClientEnt> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<ClientEnt> listOfMatchingClients = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (UserEnt user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingClients.add(UserMapper.toClient(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingClients;
    }

    @Override
    public List<AdminEnt> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<AdminEnt> listOfMatchingAdmins = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.ADMIN_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (UserEnt user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingAdmins.add(UserMapper.toAdmin(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingAdmins;
    }

    @Override
    public List<StaffEnt> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<StaffEnt> listOfMatchingStaff = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserConstants.USER_DISCRIMINATOR_NAME, UserConstants.STAFF_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (UserEnt user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingStaff.add(UserMapper.toStaff(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingStaff;
    }

    // Other read methods

    public List<TicketEnt> getListOfTicketsForClient(UUID clientID, String name) throws UserRepositoryReadException {
        try {
            List<TicketEnt> listOfActiveTickets;
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
    public void updateClient(ClientEnt client) throws UserRepositoryException {
        try {
            Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, client.getUserID());
            UserEnt updatedClientUser = getClientCollection().findOneAndReplace(clientFilter, client);
            if (updatedClientUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateAdmin(AdminEnt admin) throws UserRepositoryException {
        try {
            Bson adminFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, admin.getUserID());
            UserEnt updatedAdminUser = getClientCollection().findOneAndReplace(adminFilter, admin);
            if (updatedAdminUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.ADMIN_DOC_FOR_ADMIN_OBJ_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void updateStaff(StaffEnt staff) throws UserRepositoryException {
        try {
            Bson staffFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, staff.getUserID());
            UserEnt updatedStaffUser = getClientCollection().findOneAndReplace(staffFilter, staff);
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
    public void activate(UserEnt user, String name) throws UserRepositoryException {
        user.setUserStatusActive(true);
        try {
            this.updateCertainUserBasedOnType(user, name);
        } catch (UserRepositoryException exception) {
            throw new UserActivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    @Override
    public void deactivate(UserEnt user, String name) throws UserRepositoryException {
        user.setUserStatusActive(false);
        try {
            this.updateCertainUserBasedOnType(user, name);
        } catch (UserRepositoryException exception) {
            throw new UserDeactivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    private void updateCertainUserBasedOnType(UserEnt user, String name) throws UserRepositoryException {
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
