package services;

import com.mongodb.MongoException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.MovieRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.application_services.services.MovieService;
import pl.tks.gr3.cinema.domain_model.Movie;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepositoryAdapter movieRepositoryAdapter;

    private MovieService movieService;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    private static ArgumentCaptor<Movie> movieArgumentCaptor;

    private static Movie movieNo1;
    private static Movie movieNo2;
    private static Movie movieNo3;

    @BeforeEach
    public void initializeSampleData() {
        movieNo1 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo1", 10, 1, 10);
        movieNo2 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo2", 20, 2, 20);
        movieNo3 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo3", 30, 3, 30);
        movieService = new MovieService(movieRepositoryAdapter, movieRepositoryAdapter, movieRepositoryAdapter, movieRepositoryAdapter);
    }

    @Test
    public void movieServiceAllArgsConstructorTestPositive() {
        MovieService testMovieService = new MovieService(movieRepositoryAdapter, movieRepositoryAdapter, movieRepositoryAdapter, movieRepositoryAdapter);
        assertNotNull(testMovieService);
    }

    @Test
    public void movieServiceCreateMovieTestPositive() throws MovieServiceCreateException {
        when(movieRepositoryAdapter.create(
                Mockito.eq(movieNo1.getMovieTitle()),
                Mockito.eq(movieNo1.getMovieBasePrice()), Mockito.eq(movieNo1.getScrRoomNumber()),
                Mockito.eq(movieNo1.getNumberOfAvailableSeats()))
        ).thenReturn(movieNo1);
        Movie movie = movieService.create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());

        assertNotNull(movie);
        assertEquals(movieNo1.getMovieTitle(), movie.getMovieTitle());
        assertEquals(movieNo1.getMovieBasePrice(), movie.getMovieBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), movie.getScrRoomNumber());
        assertEquals(movieNo1.getNumberOfAvailableSeats(), movie.getNumberOfAvailableSeats());
        verify(movieRepositoryAdapter, times(1)).create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieExceptionThrown() {
        when(movieRepositoryAdapter.create(
                Mockito.eq(movieNo1.getMovieTitle()),
                Mockito.eq(movieNo1.getMovieBasePrice()), Mockito.eq(movieNo1.getScrRoomNumber()),
                Mockito.eq(movieNo1.getNumberOfAvailableSeats()))
        ).thenThrow(MovieServiceCreateException.class);

        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats()));

        verify(movieRepositoryAdapter, times(1)).create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWhoseDataDoesNotFollowConstraintsTestNegative() {
        String movieTitle = null;
        double movieBasePrice = 1;
        int scrRoomNumber = 1;
        int numberOfSeats = 2;

        when(movieRepositoryAdapter.create(Mockito.isNull(), Mockito.eq(movieBasePrice),
                Mockito.eq(scrRoomNumber), Mockito.eq(numberOfSeats))).thenThrow(MovieServiceCreateException.class);

        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfSeats));

        verify(movieRepositoryAdapter, times(1)).create(movieTitle, movieBasePrice, scrRoomNumber, numberOfSeats);
    }

    // Read tests

    @Test
    public void movieServiceFindMovieByIDTestPositive() throws MovieServiceReadException {
        when(movieRepositoryAdapter.findByUUID(Mockito.eq(movieNo1.getMovieID()))).thenReturn(movieNo1);

        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());

        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);

        verify(movieRepositoryAdapter, times(1)).findByUUID(Mockito.eq(movieNo1.getMovieID()));
    }

    @Test
    public void movieServiceFindMovieByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(movieRepositoryAdapter.findByUUID(Mockito.eq(searchedUUID))).thenThrow(MovieServiceMovieNotFoundException.class);

        assertThrows(MovieServiceMovieNotFoundException.class, () -> movieService.findByUUID(searchedUUID));

        verify(movieRepositoryAdapter, times(1)).findByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void movieServiceFindMovieByIDWhenMovieRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(movieRepositoryAdapter.findByUUID(Mockito.eq(searchedUUID))).thenThrow(MovieServiceReadException.class);

        assertThrows(MovieServiceReadException.class, () -> movieService.findByUUID(searchedUUID));

        verify(movieRepositoryAdapter, times(1)).findByUUID(searchedUUID);
    }

    @Test
    public void movieServiceFindAllMoviesTestPositive() throws MovieServiceReadException {
        when(movieRepositoryAdapter.findAll()).thenReturn(Arrays.asList(movieNo1, movieNo2, movieNo3));

        List<Movie> listOfMovies = movieService.findAll();

        assertNotNull(listOfMovies);
        assertFalse(listOfMovies.isEmpty());
        assertEquals(3, listOfMovies.size());

        verify(movieRepositoryAdapter, times(1)).findAll();
    }

    @Test
    public void movieServiceFindAllMoviesWhenMovieRepositoryExceptionIsThrownTestNegative() {
        when(movieRepositoryAdapter.findAll()).thenThrow(MovieRepositoryException.class);

        assertThrows(MovieServiceReadException.class, () -> movieService.findAll());

        verify(movieRepositoryAdapter, times(1)).findAll();
    }

//     Update tests

    @Test
    public void movieServiceUpdateMovieTestPositive() throws MovieServiceUpdateException, MovieServiceReadException {
        String movieTitleBefore = movieNo1.getMovieTitle();
        double moviePriceBefore = movieNo1.getMovieBasePrice();
        int scrRoomBefore = movieNo1.getScrRoomNumber();
        int noOfSeatsBefore = movieNo1.getNumberOfAvailableSeats();

        String newMovieTitle = "Changed title";
        double newMoviePrice = movieNo1.getMovieBasePrice() + 10;
        int newScrRoom = movieNo1.getScrRoomNumber() + 10;
        int newNoOfSeats = movieNo1.getNumberOfAvailableSeats() + 10;

        movieNo1.setMovieTitle(newMovieTitle);
        movieNo1.setMovieBasePrice(newMoviePrice);
        movieNo1.setScrRoomNumber(newScrRoom);
        movieNo1.setNumberOfAvailableSeats(newNoOfSeats);

        movieService.update(movieNo1);

        verify(movieRepositoryAdapter).update(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        String movieTitleAfter = capturedMovie.getMovieTitle();
        double movieBasePriceAfter = capturedMovie.getMovieBasePrice();
        int scrRoomAfter = capturedMovie.getScrRoomNumber();
        int noOfSeatsAfter = capturedMovie.getNumberOfAvailableSeats();

        assertNotNull(movieTitleAfter);
        assertEquals(newMovieTitle, movieTitleAfter);
        assertEquals(newMoviePrice, movieBasePriceAfter);
        assertEquals(newScrRoom, scrRoomAfter);
        assertEquals(newNoOfSeats, noOfSeatsAfter);

        assertNotEquals(movieTitleBefore, movieTitleAfter);
        assertNotEquals(moviePriceBefore, movieBasePriceAfter);
        assertNotEquals(scrRoomBefore, scrRoomAfter);
        assertNotEquals(noOfSeatsBefore, noOfSeatsAfter);

        verify(movieRepositoryAdapter, times(1)).update(movieNo1);
    }

    @Test
    public void movieServiceUpdateMovieWithDataThatDoesNotFollowConstraintsTestNegative() {
        String movieTitle = null;
        double movieBasePrice = 1;
        int scrRoomNumber = 1;
        int numberOfSeats = 2;

        movieNo1.setMovieTitle(movieTitle);
        movieNo1.setMovieBasePrice(movieBasePrice);
        movieNo1.setScrRoomNumber(scrRoomNumber);
        movieNo1.setNumberOfAvailableSeats(numberOfSeats);

        doThrow(MovieServiceUpdateException.class).when(movieRepositoryAdapter).update(movieArgumentCaptor.capture());

        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));

        verify(movieRepositoryAdapter, times(1)).update(movieNo1);
    }

    // Delete tests

    @Test
    public void movieServiceDeleteMovieTestPositive() throws MovieServiceReadException, MovieServiceDeleteException {
        UUID removedMovieUUID = movieNo1.getMovieID();

        when(movieRepositoryAdapter.findByUUID(removedMovieUUID)).thenThrow(MovieServiceDeleteException.class);

        movieService.delete(removedMovieUUID);
        verify(movieRepositoryAdapter).delete(uuidArgumentCaptor.capture());

        UUID capturedMovieUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedMovieUUID);
        assertEquals(removedMovieUUID, capturedMovieUUID);
        assertThrows(MovieServiceDeleteException.class, () -> movieService.findByUUID(removedMovieUUID));

        verify(movieRepositoryAdapter, times(1)).delete(Mockito.eq(removedMovieUUID));
        verify(movieRepositoryAdapter, times(1)).findByUUID(removedMovieUUID);
    }

    @Test
    public void movieServiceDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(MovieRepositoryException.class).when(movieRepositoryAdapter).delete(uuidArgumentCaptor.capture());

        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(exampleUUID));

        UUID capturedMovieUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedMovieUUID);

        verify(movieRepositoryAdapter, times(1)).delete(Mockito.eq(exampleUUID));
    }

    @Test
    public void movieServiceUpdateMovieWhenMovieRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";
        MongoException exception = null;

        MovieRepositoryException movieRepositoryException = new MovieRepositoryException(errorMessage, exception);

        doThrow(movieRepositoryException).when(movieRepositoryAdapter).update(any(Movie.class));

        MovieServiceUpdateException thrownException = assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(movieRepositoryAdapter, times(1)).update(any(Movie.class));
    }

}
