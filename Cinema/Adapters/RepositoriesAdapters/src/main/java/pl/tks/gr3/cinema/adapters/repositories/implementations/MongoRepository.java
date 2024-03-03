package pl.tks.gr3.cinema.adapters.repositories.implementations;

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
import pl.tks.gr3.cinema.adapters.consts.model.MovieConstants;
import pl.tks.gr3.cinema.adapters.consts.model.TicketConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserConstants;
import pl.tks.gr3.cinema.adapters.consts.repositories.MongoRepositoryConstants;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.MovieNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.TicketNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.UserNullReferenceException;
import pl.tks.gr3.cinema.adapters.messages.repositories.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MongoRepository implements Closeable {

    private final static ConnectionString connectionString = new ConnectionString("mongodb://mongodbnode1:27020, mongodbnode2:27021, mongodbnode3:27022");
    private final static MongoCredential mongoCredentials = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

    protected final static String userCollectionName = MongoRepositoryConstants.USERS_COLLECTION_NAME;
    protected final static String movieCollectionName = MongoRepositoryConstants.MOVIES_COLLECTION_NAME;
    protected final static String ticketCollectionName = MongoRepositoryConstants.TICKETS_COLLECTION_NAME;

    protected final static Class<UserEnt> clientCollectionType = UserEnt.class;
    protected final static Class<MovieEnt> movieCollectionType = MovieEnt.class;
    protected final static Class<TicketEnt> ticketCollectionType = TicketEnt.class;

    private final ClassModel<UserEnt> userClassModel = ClassModel.builder(UserEnt.class).enableDiscriminator(true).build();
    private final ClassModel<ClientEnt> clientClassModel = ClassModel.builder(ClientEnt.class).enableDiscriminator(true).build();
    private final ClassModel<AdminEnt> adminClassModel = ClassModel.builder(AdminEnt.class).enableDiscriminator(true).build();
    private final ClassModel<StaffEnt> staffClassModel = ClassModel.builder(StaffEnt.class).enableDiscriminator(true).build();
    private final ClassModel<MovieEnt> movieClassModel = ClassModel.builder(MovieEnt.class).build();
    private final ClassModel<TicketEnt> ticketClassModel = ClassModel.builder(TicketEnt.class).build();

    private final PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(
            userClassModel, clientClassModel, adminClassModel, staffClassModel, movieClassModel, ticketClassModel
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
    protected MongoCollection<UserEnt> getClientCollection() {
        return mongoDatabase.getCollection(userCollectionName, clientCollectionType);
    }

    protected MongoCollection<Document> getClientCollectionWithoutType() {
        return mongoDatabase.getCollection(userCollectionName);
    }

    protected MongoCollection<MovieEnt> getMovieCollection() {
        return mongoDatabase.getCollection(movieCollectionName, movieCollectionType);
    }

    protected MongoCollection<TicketEnt> getTicketCollection() {
        return mongoDatabase.getCollection(ticketCollectionName, ticketCollectionType);
    }

    // Find client / movie / ticket by ID

    protected UserEnt findUser(UUID userID) throws UserNullReferenceException {
        Bson clientFilter = Filters.eq(UserConstants.GENERAL_IDENTIFIER, userID);
        UserEnt user = getClientCollection().find(clientFilter).first();
        if (user != null) {
            return user;
        } else {
            throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
        }
    }

    protected MovieEnt findMovie(UUID movieID) throws MovieNullReferenceException {
        Bson movieFilter = Filters.eq(MovieConstants.GENERAL_IDENTIFIER, movieID);
        MovieEnt movie = getMovieCollection().find(movieFilter).first();
        if (movie != null) {
            return movie;
        } else {
            throw new MovieNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
        }
    }

    protected TicketEnt findTicket(UUID ticketID) throws TicketNullReferenceException {
        Bson ticketFilter = Filters.eq(TicketConstants.GENERAL_IDENTIFIER, ticketID);
        TicketEnt ticket = getTicketCollection().find(ticketFilter).first();
        if (ticket != null) {
            return ticket;
        } else {
            throw new TicketNullReferenceException(MongoRepositoryMessages.TICKET_DOC_OBJECT_NOT_FOUND);
        }
    }

    public List<TicketEnt> findTicketsWithAggregate(List<Bson> listOfFilters) {
        return getTicketCollection().aggregate(listOfFilters).into(new ArrayList<>());
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
