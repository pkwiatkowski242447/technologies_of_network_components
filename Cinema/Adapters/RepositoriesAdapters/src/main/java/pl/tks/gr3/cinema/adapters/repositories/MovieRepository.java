package pl.tks.gr3.cinema.adapters.repositories;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pl.tks.gr3.cinema.adapters.consts.model.MovieEntConstants;
import pl.tks.gr3.cinema.adapters.consts.model.TicketEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.adapters.exceptions.crud.other.MovieNullReferenceException;
import pl.tks.gr3.cinema.adapters.exceptions.other.movie.ResourceIsCurrentlyUsedDeleteException;
import pl.tks.gr3.cinema.adapters.messages.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.api.MovieRepositoryInterface;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MovieRepository extends MongoRepository implements MovieRepositoryInterface {

    private final String databaseName;
    private static final Logger logger = LoggerFactory.getLogger(MovieRepository.class);
    private final ValidationOptions validationOptions = new ValidationOptions().validator(
            Document.parse("""
                            {
                                $jsonSchema: {
                                    "bsonType": "object",
                                    "required": ["_id", "movie_title", "movie_base_price", "scr_room_number", "number_of_available_seats"],
                                    "properties": {
                                        "_id": {
                                            "description": "Id of the movie object representation in the database.",
                                            "bsonType": "binData",
                                            "pattern": "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
                                        }
                                        "movie_title": {
                                            "description": "String containing the name / title of the movie.",
                                            "bsonType": "string",
                                            "minLength": 1,
                                            "maxLength": 150
                                        }
                                        "movie_base_price": {
                                            "description": "Double value representing movie base price - before taking ticketType into account.",
                                            "bsonType": "double",
                                            "minimum": 0,
                                            "maximum": 100
                                        }
                                        "scr_room_number": {
                                            "description": "Integer value representing number of the screening room which movie is aired in.",
                                            "bsonType": "int",
                                            "minimum": 1,
                                            "maximum": 30
                                        }
                                        "number_of_available_seats": {
                                            "description": "Integer value representing maximum number of available seats inside screening room.",
                                            "bsonType": "int",
                                            "minimum": 0,
                                            "maximum": 120
                                        }
                                    }
                                }
                            }
                            """));

    public MovieRepository() {
        this.databaseName = "default";
        super.initDBConnection(this.databaseName);

        mongoDatabase.getCollection(movieCollectionName).drop();

        boolean collectionExists = false;
        for (String collectionName : mongoDatabase.listCollectionNames()) {
            if (collectionName.equals(movieCollectionName)) {
                collectionExists = true;
                break;
            }
        }

        if (!collectionExists) {
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(this.validationOptions);
            mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
        }
    }

    @PostConstruct
    private void initializeDatabaseState() {
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
    }

    @PreDestroy
    private void restoreDatabaseState() {
        try {
            List<MovieEnt> listOfAllMovies = this.findAll();
            for (MovieEnt movie : listOfAllMovies) {
                this.delete(movie.getMovieID());
            }
        } catch (MovieRepositoryException exception) {
            logger.debug(exception.getMessage());
        }
        this.close();
    }

    public MovieRepository(String databaseName) {
        this.databaseName = databaseName;
        super.initDBConnection(this.databaseName);
        mongoDatabase.getCollection(movieCollectionName).drop();
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(this.validationOptions);
        mongoDatabase.createCollection(movieCollectionName, createCollectionOptions);
    }

    // Create methods

    @Override
    public MovieEnt create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieRepositoryException {
        MovieEnt movie;
        try {
            movie = new MovieEnt(UUID.randomUUID(), movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
            getMovieCollection().insertOne(movie);
        } catch (MongoException exception) {
            throw new MovieRepositoryCreateException(exception.getMessage(), exception);
        }
        return movie;
    }

    // Read methods

    @Override
    public MovieEnt findByUUID(UUID movieID) throws MovieRepositoryException {
        try {
            return findMovie(movieID);
        } catch (MovieNullReferenceException exception) {
            throw new MovieRepositoryMovieNotFoundException(exception.getMessage(), exception);
        } catch (MongoException exception) {
            throw new MovieRepositoryReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<MovieEnt> findAll() throws MovieRepositoryException {
        List<MovieEnt> listOfAllMovies;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            Bson movieFilter = Filters.empty();
            listOfAllMovies = findMovies(movieFilter);
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new MovieRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfAllMovies;
    }

    public List<TicketEnt> getListOfTicketsForMovie(UUID movieID) {
        List<TicketEnt> listOfActiveTickets;
        List<Bson> listOfFilters = List.of(Aggregates.match(Filters.eq(TicketEntConstants.MOVIE_ID, movieID)));
        listOfActiveTickets = findTicketsWithAggregate(listOfFilters);
        return listOfActiveTickets;
    }

    // Update methods

    @Override
    public void update(MovieEnt movie) throws MovieRepositoryException {
        try {
            Bson movieFilter = Filters.eq(MovieEntConstants.GENERAL_IDENTIFIER, movie.getMovieID());
            MovieEnt updatedMovie = getMovieCollection().findOneAndReplace(movieFilter, movie);
            if (updatedMovie == null) {
                throw new MovieNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
            }
        } catch (MongoException | MovieNullReferenceException exception) {
            throw new MovieRepositoryUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    @Override
    public void delete(UUID movieID) throws MovieRepositoryException {
        try {
            Bson ticketFilter = Filters.eq(TicketEntConstants.MOVIE_ID, movieID);
            List<TicketEnt> listOfTicket = getTicketCollection().find(ticketFilter).into(new ArrayList<>());
            if (listOfTicket.isEmpty()) {
                Bson movieFilter = Filters.eq(MovieEntConstants.GENERAL_IDENTIFIER, movieID);
                MovieEnt removedMovie = getMovieCollection().findOneAndDelete(movieFilter);
                if (removedMovie == null) {
                    throw new MovieNullReferenceException(MongoRepositoryMessages.MOVIE_DOC_OBJECT_NOT_FOUND);
                }
            } else {
                throw new ResourceIsCurrentlyUsedDeleteException(MongoRepositoryMessages.MOVIE_HAS_UNFINISHED_ALLOCATIONS);
            }
        } catch (MongoException | MovieNullReferenceException | ResourceIsCurrentlyUsedDeleteException exception) {
            throw new MovieRepositoryDeleteException(exception.getMessage(), exception);
        }
    }

    private List<MovieEnt> findMovies(Bson movieFilter) throws MovieRepositoryReadException {
        List<MovieEnt> listOfFoundMovies;
        try(ClientSession clientSession = mongoClient.startSession()) {
            clientSession.startTransaction();
            listOfFoundMovies = getMovieCollection().find(movieFilter).into(new ArrayList<>());
            clientSession.commitTransaction();
        } catch (MongoException exception) {
            throw new MovieRepositoryReadException(exception.getMessage(), exception);
        }
        return listOfFoundMovies;
    }
}