package pl.tks.gr3.cinema.adapters.repositories;

import com.mongodb.*;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.*;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.connection.DatabaseConfig;
import pl.tks.gr3.cinema.adapters.consts.model.TicketEntConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.UserNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.*;
import pl.tks.gr3.cinema.adapters.exceptions.other.InvalidUUIDException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserActivationException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserDeactivationException;
import pl.tks.gr3.cinema.adapters.messages.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.adapters.api.UserRepositoryInterface;
import pl.tks.gr3.cinema.adapters.user_mappers.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@PropertySource("classpath:mongo.properties")
public class UserRepository extends MongoRepository implements UserRepositoryInterface {

    private String databaseName;
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
            Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "user_login", "user_status_active"],
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
                                        "user_status_active": {
                                            "description": "Boolean flag indicating whether user is able to perform any action.",
                                            "bsonType": "bool"
                                        }
                                    }
                                }
                            }
                            """));

    @Autowired
    public UserRepository(DatabaseConfig dbConfig) {
        String connectionString = "mongodb://%s:%s".formatted(dbConfig.getHostName(), dbConfig.getPortNumber());
        super.initDBConnection(connectionString, dbConfig.getUserName(), dbConfig.getPassword(), dbConfig.getDbName(), dbConfig.getAuthDatabase());

        mongoDatabase.getCollection(userCollectionName).drop();

        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(userCollectionName, createCollectionOptions);
        IndexOptions indexOptions = new IndexOptions().unique(true);
        mongoDatabase.getCollection(userCollectionName).createIndex(Indexes.ascending(UserEntConstants.USER_LOGIN), indexOptions);
    }

    public UserRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(userCollectionName).drop();

        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(userCollectionName, createCollectionOptions);
        IndexOptions indexOptions = new IndexOptions().unique(true);
        mongoDatabase.getCollection(userCollectionName).createIndex(Indexes.ascending(UserEntConstants.USER_LOGIN), indexOptions);
    }

    public UserRepository(String connectionString, String login, String password, String database) {
        super.initDBConnection(connectionString, login, password, database);

        mongoDatabase.getCollection(userCollectionName).drop();

        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(userCollectionName, createCollectionOptions);
        IndexOptions indexOptions = new IndexOptions().unique(true);
        mongoDatabase.getCollection(userCollectionName).createIndex(Indexes.ascending(UserEntConstants.USER_LOGIN), indexOptions);
    }

    @PostConstruct
    public void initializeDatabaseState() {
        UUID clientIDNo1 = UUID.fromString("26c4727c-c791-4170-ab9d-faf7392e80b2");
        UUID clientIDNo2 = UUID.fromString("0b08f526-b018-4d23-8baa-93f0fb884edf");
        UUID clientIDNo3 = UUID.fromString("30392328-2cae-4e76-abb8-b1aa8f58a9e4");

        ClientEnt clientNo1 = new ClientEnt(clientIDNo1, "NewClientLogin1");
        ClientEnt clientNo2 = new ClientEnt(clientIDNo2, "NewClientLogin2");
        ClientEnt clientNo3 = new ClientEnt(clientIDNo3, "NewClientLogin3");

        List<UserEnt> listOfClients = List.of(clientNo1, clientNo2, clientNo3);
        for (UserEnt user : listOfClients) {
            Bson filter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, user.getUserID());
            if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(ClientEnt.class)) {
                this.getClientCollection().insertOne(user);
            }
        }
    }

    // Create methods

    @Override
    public ClientEnt createClient(UUID clientId, String clientLogin) throws UserRepositoryException {
        ClientEnt client;
        try {
            client = new ClientEnt(clientId, clientLogin);
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

    // Read methods

    public UserEnt findByUUID(UUID clientID) throws UserRepositoryReadException {
        UserEnt user;
        try {
            user = super.findUser(clientID);
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return user;
    }

    // Find users by UUID

    public ClientEnt findClientByUUID(UUID clientID) throws UserRepositoryReadException {
        ClientEnt client;
        try {
            List<Bson> aggregate = List.of(Aggregates.match(Filters.eq(UserEntConstants.USER_DISCRIMINATOR_NAME, UserEntConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, clientID)));
            UserEnt foundClientUser = getClientCollection().aggregate(aggregate).first();
            if (foundClientUser != null) {
                client = UserMapper.toClientEnt(foundClientUser);
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

    // Find all users methods

    @Override
    public List<ClientEnt> findAllClients() throws UserRepositoryException {
        List<ClientEnt> listOfAllClients = new ArrayList<>();
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson filter = Filters.eq(UserEntConstants.USER_DISCRIMINATOR_NAME, UserEntConstants.CLIENT_DISCRIMINATOR);
            List<UserEnt> listOfClientUsers = getClientCollection().find(filter).into(new ArrayList<>());
            for (UserEnt clientUser : listOfClientUsers) {
                listOfAllClients.add(UserMapper.toClientEnt(clientUser));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllClients;
    }

    // Find users by login

    @Override
    public ClientEnt findClientByLogin(String loginValue) throws UserRepositoryException {
        ClientEnt client;
        try {
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserEntConstants.USER_DISCRIMINATOR_NAME, UserEntConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.eq(UserEntConstants.USER_LOGIN, loginValue)));
            UserEnt foundClientUser = getClientCollection().aggregate(listOfFilters).first();
            if (foundClientUser != null) {
                client = UserMapper.toClientEnt(foundClientUser);
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

    // Find all users matching login

    @Override
    public List<ClientEnt> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException {
        List<ClientEnt> listOfMatchingClients = new ArrayList<>();
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(UserEntConstants.USER_DISCRIMINATOR_NAME, UserEntConstants.CLIENT_DISCRIMINATOR)),
                    Aggregates.match(Filters.regex(UserEntConstants.USER_LOGIN, "^" + loginValue + ".*$")));
            for (UserEnt user : getClientCollection().aggregate(listOfFilters)) {
                listOfMatchingClients.add(UserMapper.toClientEnt(user));
            }
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new UserRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfMatchingClients;
    }

    // Other read methods

    public List<TicketEnt> getListOfTicketsForClient(UUID clientID, String name) throws UserRepositoryReadException {
        try {
            List<TicketEnt> listOfActiveTickets;
            Bson clientFilter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, clientID);
            Document user = getClientCollectionWithoutType().find(clientFilter).first();
            if (user == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.DOC_OBJECT_NOT_FOUND);
            } else if (user.getString(UserEntConstants.USER_DISCRIMINATOR_NAME).equals(name)) {
                List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(TicketEntConstants.USER_ID, clientID)));
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
            Bson clientFilter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, client.getUserID());
            UserEnt updatedClientUser = getClientCollection().findOneAndReplace(clientFilter, client);
            if (updatedClientUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    public void delete(UUID userID) throws UserRepositoryException {
        try {
            Bson filter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, userID);
            Document clientDoc = getClientCollectionWithoutType().find(filter).first();
            if (clientDoc == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.DOC_OBJECT_NOT_FOUND);
            } else {
                getClientCollection().findOneAndDelete(filter);
            }
        } catch (MongoException | UserNullReferenceException exception) {
            throw new UserRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UserEnt user) throws UserRepositoryException {
        user.setUserStatusActive(true);
        try {
            this.updateCertainUserBasedOnType(user);
        } catch (UserRepositoryException exception) {
            throw new UserActivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    @Override
    public void deactivate(UserEnt user) throws UserRepositoryException {
        user.setUserStatusActive(false);
        try {
            this.updateCertainUserBasedOnType(user);
        } catch (UserRepositoryException exception) {
            throw new UserDeactivationException(MongoRepositoryMessages.USER_DOC_FOR_CLIENT_OBJ_NOT_FOUND);
        }
    }

    private void updateCertainUserBasedOnType(UserEnt user) throws UserRepositoryException {
        this.updateClient(UserMapper.toClientEnt(user));
    }
}
