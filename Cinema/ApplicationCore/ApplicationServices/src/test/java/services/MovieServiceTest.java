package services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.application_services.services.MovieService;
import pl.tks.gr3.cinema.adapters.aggregates.MovieRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.repositories.MovieRepository;
import pl.tks.gr3.cinema.domain_model.model.Movie;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private static final String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(MovieServiceTest.class);

    private static MovieRepository movieRepository;
    private static MovieService movieService;

    private Movie movieNo1;
    private Movie movieNo2;

    @BeforeAll
    public static void initialize() {
        movieRepository = new MovieRepository(databaseName);
        movieService = new MovieService(new MovieRepositoryAdapter(movieRepository));
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            movieNo1 = movieService.create("UniqueMovieTitleNo1", 45.00, 1, 60);
            movieNo2 = movieService.create("UniqueMovieTitleNo2", 35.50, 2, 40);
        } catch (MovieServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        try {
            List<Movie> listOfMovies = movieService.findAll();
            for (Movie movie : listOfMovies) {
                movieService.delete(movie.getMovieID());
            }
        } catch (MovieServiceReadException | MovieServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        movieRepository.close();
    }

    // Constructor tests
    
    @Test
    public void movieServiceNoArgsConstructorTestPositive() {
        MovieService testMovieService = new MovieService();
        assertNotNull(testMovieService);
    }

    @Test
    public void movieServiceAllArgsConstructorTestPositive() {
        MovieService testMovieService = new MovieService(new MovieRepositoryAdapter(movieRepository));
        assertNotNull(testMovieService);
    }
    
    // Create tests

    @Test
    public void movieServiceCreateMovieTestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithNullMovieTitleTestNegative() {
        String movieTitle = null;
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithEmptyMovieTitleTestNegative() {
        String movieTitle = "";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleTooShortTestNegative() {
        String movieTitle = "";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleTooLongTestNegative() {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleLengthEqualTo1TestPositive() throws MovieServiceCreateException {
        String movieTitle = "d";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleLengthEqualTo150TestPositive() throws MovieServiceCreateException {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithNegativeMovieBasePriceTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = -1.00;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithMovieBasePriceTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 101.50;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 0;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 0;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 31;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 1;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = -1;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 121;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieServiceCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 0;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 120;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    // Read tests

    @Test
    public void movieServiceFindMovieByIDTestPositive() throws MovieServiceReadException {
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
    }

    @Test
    public void movieServiceFindMovieByIDThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeExampleTitleNo1", 25.75, 4, 40);
        assertNotNull(movie);
        assertThrows(MovieServiceMovieNotFoundException.class, () -> movieService.findByUUID(movie.getMovieID()));
    }

    @Test
    public void movieServiceFindAllMoviesTestPositive() throws MovieServiceReadException {
        List<Movie> listOfMovies = movieService.findAll();
        assertNotNull(listOfMovies);
        assertFalse(listOfMovies.isEmpty());
        assertEquals(2, listOfMovies.size());
    }

    // Update tests

    @Test
    public void movieServiceUpdateMovieTestPositive() {
        String movieTitleBefore = movieNo1.getMovieTitle();
        double movieBasePriceBefore = movieNo1.getMovieBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getNumberOfAvailableSeats();

        String newMovieTitle = "SomeNewMovieTitleNo1";
        double newMovieBasePrice = 50.25;
        int newScrRoomNumber = 19;
        int newNumberOfAvailableSeats = 27;

        movieNo1.setMovieTitle(newMovieTitle);
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);

        movieService.update(movieNo1);

        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());

        String movieTitleAfter = foundMovie.getMovieTitle();
        double movieBasePriceAfter = foundMovie.getMovieBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableAfter = foundMovie.getNumberOfAvailableSeats();

        assertEquals(newMovieTitle, movieTitleAfter);
        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertEquals(newNumberOfAvailableSeats, numberOfAvailableAfter);

        assertNotEquals(movieTitleBefore, movieTitleAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableAfter);
    }

    @Test
    public void movieServiceUpdateMovieWithNullMovieTitleTestNegative() {
        String newMovieTitle = null;
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleTestNegative() {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleTooShortTestNegative() {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleTooLongTestNegative() {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleLengthEqualTo1TestPositive() {
        String newMovieTitle = "d";
        movieNo1.setMovieTitle(newMovieTitle);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieTitle, foundMovie.getMovieTitle());
    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleLengthEqualTo150TestPositive() {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        movieNo1.setMovieTitle(newMovieTitle);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieTitle, foundMovie.getMovieTitle());
    }

    @Test
    public void movieServiceUpdateMovieWithNegativeMovieBasePriceTestNegative() {
        double newMovieBasePrice = -1.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithMovieBasePriceTooHighTestNegative() {
        double newMovieBasePrice = 101.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithMovieBasePriceEqualTo0TestPositive() {
        double newMovieBasePrice = 0;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getMovieBasePrice());
    }

    @Test
    public void movieServiceUpdateMovieWithMovieBasePriceEqualTo100TestPositive() {
        double newMovieBasePrice = 100;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getMovieBasePrice());
    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int newScrRoomNumber = 0;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int newScrRoomNumber = 31;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieServiceReadException {
        int newScrRoomNumber = 1;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newScrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() {
        int newScrRoomNumber = 30;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newScrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    public void movieServiceUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        int newNumberOfAvailableSeats = -1;
        movieNo1.setScrRoomNumber(newNumberOfAvailableSeats);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int newNumberOfAvailableSeats = 121;
        movieNo1.setScrRoomNumber(newNumberOfAvailableSeats);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieServiceUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {
        int newNumberOfAvailableSeats = 0;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newNumberOfAvailableSeats, foundMovie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceUpdateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() {
        int newNumberOfAvailableSeats = 120;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newNumberOfAvailableSeats, foundMovie.getNumberOfAvailableSeats());
    }

    // Delete tests

    @Test
    public void movieServiceDeleteMovieTestPositive() throws MovieServiceReadException, MovieServiceDeleteException {
        UUID removedMovieUUID = movieNo1.getMovieID();
        Movie foundMovie = movieService.findByUUID(removedMovieUUID);
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
        movieService.delete(removedMovieUUID);
        assertThrows(MovieServiceReadException.class, () -> movieService.findByUUID(removedMovieUUID));
    }

    @Test
    public void movieServiceDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeExampleTitleNo1", 25.75, 4, 40);
        assertNotNull(movie);
        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(movie.getMovieID()));
    }
}