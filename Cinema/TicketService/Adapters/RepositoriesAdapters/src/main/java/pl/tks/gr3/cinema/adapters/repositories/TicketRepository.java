package pl.tks.gr3.cinema.adapters.repositories;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.connection.DatabaseConfig;
import pl.tks.gr3.cinema.adapters.consts.model.MovieEntConstants;
import pl.tks.gr3.cinema.adapters.consts.model.TicketEntConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.TicketRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.MovieNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.ObjectNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.TicketNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.UserNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.ticket.*;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.ClientNotActiveException;
import pl.tks.gr3.cinema.adapters.messages.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.adapters.api.TicketRepositoryInterface;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@PropertySource("classpath:mongo.properties")
public class TicketRepository extends MongoRepository implements TicketRepositoryInterface {

    private String databaseName;
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
            Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "movie_time", "movie_final_price", "user_id", "movie_id"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the ticket object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "movie_time": {
                                            "description": "Variable holding value of the date, when movie will air.",
                                            "bsonType": "date"
                                        }
                                        "ticket_final_price": {
                                            "description": "Double value holding final price of the ticket, that is after applying all discounts.",
                                            "bsonType": "double"
                                        }
                                        "user_id": {
                                            "description": "Id of the user object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                        "movie_id": {
                                            "description": "Id of the movie object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                                        }
                                    }
                                }
                            }
                            """));

    @Autowired
    public TicketRepository(DatabaseConfig dbConfig) {
        String connectionString = "mongodb://%s:%s".formatted(dbConfig.getHostName(), dbConfig.getPortNumber());
        super.initDBConnection(connectionString, dbConfig.getUserName(), dbConfig.getPassword(), dbConfig.getDbName(), dbConfig.getAuthDatabase());

        mongoDatabase.getCollection(ticketCollectionName).drop();

        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
    }

    public TicketRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(ticketCollectionName).drop();

        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
    }

    public TicketRepository(String connectionString, String login, String password, String databaseName) {
        super.initDBConnection(connectionString, login, password, databaseName);

        mongoDatabase.getCollection(ticketCollectionName).drop();

        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        mongoDatabase.createCollection(ticketCollectionName, createCollectionOptions);
    }

    @PostConstruct
    private void initializeDatabaseState() {
        // Client data

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
            Bson filter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, user.getUserID());
            if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(ClientEnt.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(AdminEnt.class)) {
                this.getClientCollection().insertOne(user);
            } else if (this.getClientCollection().find(filter).first() == null && user.getClass().equals(StaffEnt.class)) {
                this.getClientCollection().insertOne(user);
            }
        }

        // Movie data

        UUID movieNo1ID = UUID.fromString("f3e66584-f793-4f5e-9dec-904ca00e2dd6");
        UUID movieNo2ID = UUID.fromString("9b9e1de2-099b-415d-96b4-f7cfc8897318");
        UUID movieNo3ID = UUID.fromString("b69b4714-e307-4ebf-b491-e3720f963f53");

        MovieEnt movieNo1 = new MovieEnt(movieNo1ID, "Pulp Fiction", 45.75, 1, 100);
        MovieEnt movieNo2 = new MovieEnt(movieNo2ID, "Cars", 30.50, 2, 50);
        MovieEnt movieNo3 = new MovieEnt(movieNo3ID, "Joker", 50.00, 3, 75);

        List<MovieEnt> listOfMovies = List.of(movieNo1, movieNo2, movieNo3);
        for (MovieEnt movie : listOfMovies) {
            Bson filter = Filters.eq(MovieEntConstants.GENERAL_IDENTIFIER, movie.getMovieID());
            if (this.getMovieCollection().find(filter).first() == null) {
                this.getMovieCollection().insertOne(movie);
            }
        }

        // Creating tickets

        UUID ticketIDNo1 = UUID.fromString("a0ed1047-b56b-4e22-b797-c5a28df24d11");
        UUID ticketIDNo2 = UUID.fromString("1caa19c8-12c5-45ae-8019-ba93ba83a927");
        LocalDateTime movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(4).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime movieTimeNo2 = LocalDateTime.now().plusDays(4).plusHours(2).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);

        TicketEnt ticketNo1 = new TicketEnt(ticketIDNo1, movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        TicketEnt ticketNo2 = new TicketEnt(ticketIDNo2, movieTimeNo2, movieNo2.getMovieBasePrice(), clientNo2.getUserID(), movieNo2.getMovieID());

        this.getTicketCollection().insertOne(ticketNo1);
        this.getTicketCollection().insertOne(ticketNo2);
    }

    @Override
    public TicketEnt create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException {
        TicketEnt ticket;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson clientFilter = Filters.eq(UserEntConstants.GENERAL_IDENTIFIER, clientID);
            UserEnt foundClientUser = getClientCollection().find(clientFilter).first();
            if (foundClientUser == null) {
                throw new UserNullReferenceException(MongoRepositoryMessages.CLIENT_DOC_OBJECT_NOT_FOUND);
            } else if (!foundClientUser.isUserStatusActive()) {
                throw new ClientNotActiveException(MongoRepositoryMessages.ALLOCATION_NOT_POSSIBLE_SINCE_CLIENT_INACTIVE);
            }

            Bson movieFilter = Filters.eq(MovieEntConstants.GENERAL_IDENTIFIER, movieID);
            MovieEnt foundMovie = getMovieCollection().find(movieFilter).first();
            if (foundMovie == null) {
                throw new MovieNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
            }

            Bson update = Updates.inc(MovieEntConstants.NUMBER_OF_AVAILABLE_SEATS, -1);
            getMovieCollection().updateOne(movieFilter, update);

            ticket = new TicketEnt(UUID.randomUUID(), movieTime, foundMovie.getMovieBasePrice(), clientID, movieID);
            getTicketCollection().insertOne(ticket);

            clientSession.commitTransaction();
        } catch (MongoException |
                 ObjectNullReferenceException |
                 ClientNotActiveException exception) {
            throw new TicketRepositoryCreateException(exception.getMessage(), exception);
        }
        return ticket;
    }

    @Override
    public TicketEnt findByUUID(UUID ticketID) throws TicketRepositoryException {
        try {
            return findTicket(ticketID);
        } catch (TicketNullReferenceException exception) {
            throw new TicketRepositoryTicketNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<TicketEnt> findAll() throws TicketRepositoryException {
        List<TicketEnt> listOfAllTickets;
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.empty();
            listOfAllTickets = findTickets(ticketFilter);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllTickets;
    }

    @Override
    public void update(TicketEnt ticket) throws TicketRepositoryException {
        try {
            Bson ticketFilter = Filters.eq("_id", ticket.getTicketID());
            TicketEnt updatedTicket = getTicketCollection().findOneAndReplace(ticketFilter, ticket);
            if (updatedTicket == null) {
                throw new TicketNullReferenceException(MongoRepositoryMessages.TICKET_DOC_FOR_TICKET_OBJ_NOT_FOUND);
            }
        } catch (MongoException | TicketNullReferenceException exception) {
            throw new TicketRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID ticketID) throws TicketRepositoryException {
        try (ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson ticketFilter = Filters.eq(TicketEntConstants.GENERAL_IDENTIFIER, ticketID);
            TicketEnt removedTicket = getTicketCollection().findOneAndDelete(ticketFilter);
            if (removedTicket != null) {
                Bson movieFilter = Filters.eq(MovieEntConstants.GENERAL_IDENTIFIER, removedTicket.getMovieID());
                Bson update = Updates.inc(MovieEntConstants.NUMBER_OF_AVAILABLE_SEATS, 1);
                getMovieCollection().updateOne(movieFilter, update);
            } else {
                throw new TicketNullReferenceException(MongoRepositoryMessages.TICKET_DOC_OBJECT_NOT_FOUND);
            }
            clientSession.commitTransaction();
        } catch (MongoException | NullPointerException | ObjectNullReferenceException exception) {
            throw new TicketRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    private List<TicketEnt> findTickets(Bson ticketFilter) throws TicketRepositoryReadException {
        List<TicketEnt> listOfFoundTickets;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            listOfFoundTickets = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new TicketRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfFoundTickets;
    }
}