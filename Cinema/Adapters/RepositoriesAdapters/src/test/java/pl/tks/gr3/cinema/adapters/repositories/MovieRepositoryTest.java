package pl.tks.gr3.cinema.adapters.repositories;

import org.junit.jupiter.api.*;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovieRepositoryTest extends TestContainerSetup {

    private MovieEnt movieNo1;
    private MovieEnt movieNo2;
    private MovieEnt movieNo3;

    @BeforeEach
    public void addExampleMovies() {
        // Initialize sample data
        try {
            movieNo1 = movieRepository.create("ExampleTitleNo1", 10.50, 1, 30);
            movieNo2 = movieRepository.create("ExampleTitleNo2", 23.75, 2, 45);
            movieNo3 = movieRepository.create("ExampleTitleNo3", 40.25, 3, 60);
        } catch (MovieRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding movies to it.", exception);
        }
    }

    @AfterEach
    public void removeExampleMovies() {
        // Remove sample data
        try {
            List<MovieEnt> listOfAllMovies = movieRepository.findAll();
            for (MovieEnt movie : listOfAllMovies) {
                movieRepository.delete(movie.getMovieID());
            }
        } catch (MovieRepositoryException exception) {
            throw new RuntimeException("Could not remove all movies from the test database after movie repository tests.", exception);
        }
    }

    @Test
    public void movieRepositoryCreateMovieTestPositive() throws MovieRepositoryException {
        MovieEnt movie = movieRepository.create("OtherMovieTitleNo1", 40.75, 10, 90);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryCreateMovieWithNullMovieTitleTestNegative() {
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create(null, 50.00, 10, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithEmptyMovieTitleTestNegative() {
        String movieTitle = "";
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create(movieTitle, 50.00, 10, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithMovieTitleTooLongTestNegative() {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf1";
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create(movieTitle, 50.00, 10, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithNegativeMovieBasePriceTestNegative() {
        double movieBasePrice = -10.00;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", movieBasePrice, 10, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithMovieBasePriceTooHighTestNegative() {
        double movieBasePrice = 150.00;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", movieBasePrice, 10, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieRepositoryException {
        double movieBasePrice = 0.00;
        MovieEnt movie = movieRepository.create("SomeMovieTitle", movieBasePrice, 10, 90);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryCreateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieRepositoryException {
        double movieBasePrice = 100.00;
        MovieEnt movie = movieRepository.create("SomeMovieTitle", movieBasePrice, 10, 90);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryCreateMovieWithScreeningRoomNumberNegativeTestNegative() {
        int scrRoomNumber = -1;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", 50, scrRoomNumber, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int scrRoomNumber = 100;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", 50, scrRoomNumber, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int scrRoomNumber = 0;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", 50, scrRoomNumber, 90));
    }

    @Test
    public void movieRepositoryCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieRepositoryException {
        int scrRoomNumber = 1;
        MovieEnt movie = movieRepository.create("SomeMovieTitle", 50.00, scrRoomNumber, 90);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieRepositoryException {
        int scrRoomNumber = 30;
        MovieEnt movie = movieRepository.create("SomeMovieTitle", 50.00, scrRoomNumber, 90);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryCreateMovieWithNumberOfAvailableSeatsNegativeTestNegative() {
        int numberOfAvailableSeats = -1;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", 50, 1, numberOfAvailableSeats));
    }

    @Test
    public void movieRepositoryCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int numberOfAvailableSeats = 121;
        assertThrows(MovieRepositoryCreateException.class, () -> movieRepository.create("SomeMovieTitle", 50, 1, numberOfAvailableSeats));
    }

    @Test
    public void movieRepositoryCreateMovieWithNumberOfAvailableSeatsEqualTo1TestPositive() throws MovieRepositoryException {
        int numberOfAvailableSeats = 0;
        MovieEnt movie = movieRepository.create("SomeMovieTitle", 50.00, 1, numberOfAvailableSeats);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieRepositoryException {
        int numberOfAvailableSeats = 120;
        MovieEnt movie = movieRepository.create("SomeMovieTitle", 50.00, 1, numberOfAvailableSeats);
        assertNotNull(movie);
        MovieEnt foundMovie = movieRepository.findByUUID(movie.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    public void movieRepositoryFindMovieTestPositive() throws MovieRepositoryException {
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
    }

    @Test
    public void movieRepositoryFindMovieThatIsNotInTheDatabaseTestNegative() {
        MovieEnt movie = new MovieEnt(UUID.randomUUID(), "SomeMovieTitle", 50.00, 10, 90);
        assertNotNull(movie);
        assertThrows(MovieRepositoryMovieNotFoundException.class, () -> movieRepository.findByUUID(movie.getMovieID()));
    }

    @Test
    public void movieRepositoryFindAllMoviesTestPositive() throws MovieRepositoryException {
        List<MovieEnt> listOfAllMovies = movieRepository.findAll();
        assertNotNull(listOfAllMovies);
        assertFalse(listOfAllMovies.isEmpty());
        assertEquals(3, listOfAllMovies.size());
    }

    @Test
    public void movieRepositoryUpdateMovieTestPositive() throws MovieRepositoryException {
        String movieTitleBefore = movieNo1.getMovieTitle();
        String newMovieTitle = "Pulp Fiction";
        movieNo1.setMovieTitle(newMovieTitle);
        movieRepository.update(movieNo1);
        String movieTitleAfter = movieNo1.getMovieTitle();
        assertNotNull(movieTitleAfter);
        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    public void movieRepositoryUpdateMovieThatIsNotInTheDatabaseTestNegative() {
        MovieEnt movie = new MovieEnt(UUID.randomUUID(), "SomeMovieTitle", 50.00, 10, 90);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movie));
    }

    @Test
    public void movieRepositoryUpdateMovieWithNullMovieTitleTestNegative() {
        movieNo1.setMovieTitle(null);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithEmptyMovieTitleTestNegative() {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithMovieTitleTooLongTestNegative() {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf1";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithMovieBasePriceNegativeTestNegative() {
        double newMovieBasePrice = -10.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithMovieBasePriceTooHighTestNegative() {
        double newMovieBasePrice = 110;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithMovieBasePriceEqualTo0HighTestNegative() throws MovieRepositoryException {
        double newMovieBasePrice = 0.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieRepository.update(movieNo1);
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getMovieBasePrice());
    }

    @Test
    public void movieRepositoryUpdateMovieWithMovieBasePriceEqualTo100HighTestNegative() throws MovieRepositoryException {
        double newMovieBasePrice = 100.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieRepository.update(movieNo1);
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getMovieBasePrice());
    }

    @Test
    public void movieRepositoryUpdateMovieWithScreeningRoomNumberNegativeTestNegative() {
        int scrRoomNumber = -1;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int scrRoomNumber = 151;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int scrRoomNumber = 0;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithScreeningRoomNumberEqualTo1HighTestNegative() throws MovieRepositoryException {
        int scrRoomNumber = 1;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        movieRepository.update(movieNo1);
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(scrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    public void movieRepositoryUpdateMovieWithScreeningRoomNumberEqualTo30HighTestNegative() throws MovieRepositoryException {
        int scrRoomNumber = 30;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        movieRepository.update(movieNo1);
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(scrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    public void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsNegativeTestNegative() {
        int numberOfAvailableSeats = -1;
        movieNo1.setNumberOfAvailableSeats(numberOfAvailableSeats);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int numberOfAvailableSeats = 151;
        movieNo1.setNumberOfAvailableSeats(numberOfAvailableSeats);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepository.update(movieNo1));
    }

    @Test
    public void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsEqualTo0HighTestPositive() throws MovieRepositoryException {
        int numberOfAvailableSeats = 0;
        movieNo1.setNumberOfAvailableSeats(numberOfAvailableSeats);
        movieRepository.update(movieNo1);
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(numberOfAvailableSeats, foundMovie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsEqualTo150HighTestNegative() throws MovieRepositoryException {
        int numberOfAvailableSeats = 30;
        movieNo1.setNumberOfAvailableSeats(numberOfAvailableSeats);
        movieRepository.update(movieNo1);
        MovieEnt foundMovie = movieRepository.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(numberOfAvailableSeats, foundMovie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieRepositoryDeleteMovieTestPositive() throws MovieRepositoryException {
        int numberOfMoviesBefore = movieRepository.findAll().size();
        UUID removedMovieUUID = movieNo1.getMovieID();
        movieRepository.delete(removedMovieUUID);
        int numberOfMoviesAfter = movieRepository.findAll().size();
        assertNotEquals(numberOfMoviesBefore, numberOfMoviesAfter);
        assertEquals(3, numberOfMoviesBefore);
        assertEquals(2, numberOfMoviesAfter);
        assertThrows(MovieRepositoryReadException.class, () -> movieRepository.findByUUID(removedMovieUUID));
    }

    @Test
    public void movieRepositoryDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        MovieEnt movie = new MovieEnt(UUID.randomUUID(), "SomeMovieTitle", 50.00, 10, 90);
        assertThrows(MovieRepositoryDeleteException.class, () -> movieRepository.delete(movie.getMovieID()));
    }
}