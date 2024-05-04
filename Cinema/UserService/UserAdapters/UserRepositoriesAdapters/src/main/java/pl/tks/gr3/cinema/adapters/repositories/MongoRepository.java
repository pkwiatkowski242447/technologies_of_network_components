package pl.tks.gr3.cinema.adapters.repositories;

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
import pl.tks.gr3.cinema.adapters.connection.MongoDBConnector;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.consts.MongoRepositoryConstants;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.UserNullReferenceException;
import pl.tks.gr3.cinema.adapters.messages.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.UserEnt;

import java.util.List;
import java.util.UUID;

public abstract class MongoRepository implements AutoCloseable {

    protected final static String userCollectionName = MongoRepositoryConstants.USERS_COLLECTION_NAME;

    protected final static Class<UserEnt> clientCollectionType = UserEnt.class;

    private final ClassModel<UserEnt> userClassModel = ClassModel.builder(UserEnt.class).enableDiscriminator(true).build();
    private final ClassModel<ClientEnt> clientClassModel = ClassModel.builder(ClientEnt.class).enableDiscriminator(true).build();
    private final ClassModel<AdminEnt> adminClassModel = ClassModel.builder(AdminEnt.class).enableDiscriminator(true).build();
    private final ClassModel<StaffEnt> staffClassModel = ClassModel.builder(StaffEnt.class).enableDiscriminator(true).build();

    private final PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
            userClassModel, clientClassModel, adminClassModel, staffClassModel
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
        MongoDBConnector.readConnectionData();

        String connectionString = "mongodb://" + MongoDBConnector.getMongoHost() + ":" + MongoDBConnector.getMongoPort();

        MongoCredential mongoCredentials = MongoCredential.createCredential(
                MongoDBConnector.getUsername(),
                MongoDBConnector.getMongoDatabase(),
                MongoDBConnector.getPassword().toCharArray()
        );

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .credential(mongoCredentials)
                .readConcern(ReadConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY)
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    protected void initDBConnection(String connectionString, String login, String password, String databaseName) {
        MongoDBConnector.readConnectionData();

        MongoCredential mongoCredentials = MongoCredential.createCredential(
                login,
                "admin",
                password.toCharArray()
        );

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .credential(mongoCredentials)
                .readConcern(ReadConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY)
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    protected void initDBConnection(String connectionString, String login, String password, String databaseName, String authDatabaseName) {
        MongoCredential mongoCredentials = MongoCredential.createCredential(
                login,
                authDatabaseName,
                password.toCharArray()
        );

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .credential(mongoCredentials)
                .readConcern(ReadConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY)
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    protected MongoCollection<UserEnt> getClientCollection() {
        return mongoDatabase.getCollection(userCollectionName, clientCollectionType);
    }

    protected MongoCollection<Document> getClientCollectionWithoutType() {
        return mongoDatabase.getCollection(userCollectionName);
    }

    // Find client / movie / ticket by ID

    protected UserEnt findUser(UUID userID) throws UserNullReferenceException {
        Bson clientFilter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, userID);
        UserEnt user = getClientCollection().find(clientFilter).first();
        if (user != null) {
            return user;
        } else {
            throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
        }
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
