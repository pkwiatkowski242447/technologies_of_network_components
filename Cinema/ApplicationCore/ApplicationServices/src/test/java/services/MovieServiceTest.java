package services;

import com.mongodb.MongoException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.MovieRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.movie.MovieRepositoryMovieNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.movie.*;
import pl.tks.gr3.cinema.application_services.services.MovieService;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.ports.infrastructure.movies.CreateMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.DeleteMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.ReadMoviePort;
import pl.tks.gr3.cinema.ports.infrastructure.movies.UpdateMoviePort;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private CreateMoviePort createMoviePort;

    @Mock
    private ReadMoviePort readMoviePort;

    @Mock
    private UpdateMoviePort updateMoviePort;

    @Mock
    private DeleteMoviePort deleteMoviePort;

    @InjectMocks
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
    }

    @Test
    public void movieServiceAllArgsConstructorTestPositive() {
        MovieService testMovieService = new MovieService(createMoviePort, readMoviePort, updateMoviePort, deleteMoviePort);
        assertNotNull(testMovieService);
    }

    @Test
    public void movieServiceCreateMovieTestPositive() throws MovieServiceCreateException {
        when(createMoviePort.create(
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
        verify(createMoviePort, times(1)).create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieExceptionThrownTestNegative() {
        when(createMoviePort.create(
                Mockito.eq(movieNo1.getMovieTitle()),
                Mockito.eq(movieNo1.getMovieBasePrice()), Mockito.eq(movieNo1.getScrRoomNumber()),
                Mockito.eq(movieNo1.getNumberOfAvailableSeats()))
        ).thenThrow(MovieRepositoryException.class);

        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats()));

        verify(createMoviePort, times(1)).create(movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(),
                movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieServiceCreateMovieWhoseDataDoesNotFollowConstraintsTestNegative() {
        String movieTitle = null;
        double movieBasePrice = 1;
        int scrRoomNumber = 1;
        int numberOfSeats = 2;

        when(createMoviePort.create(Mockito.isNull(), Mockito.eq(movieBasePrice),
                Mockito.eq(scrRoomNumber), Mockito.eq(numberOfSeats))).thenThrow(MovieServiceCreateException.class);

        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfSeats));

        verify(createMoviePort, times(1)).create(movieTitle, movieBasePrice, scrRoomNumber, numberOfSeats);
    }

    // Read tests

    @Test
    public void movieServiceFindMovieByIDTestPositive() throws MovieServiceReadException {
        when(readMoviePort.findByUUID(Mockito.eq(movieNo1.getMovieID()))).thenReturn(movieNo1);

        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());

        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);

        verify(readMoviePort, times(1)).findByUUID(Mockito.eq(movieNo1.getMovieID()));
    }

    @Test
    public void movieServiceFindMovieByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readMoviePort.findByUUID(Mockito.eq(searchedUUID))).thenThrow(MovieRepositoryMovieNotFoundException.class);

        assertThrows(MovieServiceMovieNotFoundException.class, () -> movieService.findByUUID(searchedUUID));

        verify(readMoviePort, times(1)).findByUUID(Mockito.eq(searchedUUID));
    }

    @Test
    public void movieServiceFindMovieByIDWhenMovieRepositoryExceptionIsThrownTestNegative() {
        UUID searchedUUID = UUID.randomUUID();

        when(readMoviePort.findByUUID(Mockito.eq(searchedUUID))).thenThrow(MovieRepositoryException.class);

        assertThrows(MovieServiceReadException.class, () -> movieService.findByUUID(searchedUUID));

        verify(readMoviePort, times(1)).findByUUID(searchedUUID);
    }

    @Test
    public void movieServiceFindAllMoviesTestPositive() throws MovieServiceReadException {
        when(readMoviePort.findAll()).thenReturn(Arrays.asList(movieNo1, movieNo2, movieNo3));

        List<Movie> listOfMovies = movieService.findAll();

        assertNotNull(listOfMovies);
        assertFalse(listOfMovies.isEmpty());
        assertEquals(3, listOfMovies.size());

        verify(readMoviePort, times(1)).findAll();
    }

    @Test
    public void movieServiceFindAllMoviesWhenMovieRepositoryExceptionIsThrownTestNegative() {
        when(readMoviePort.findAll()).thenThrow(MovieRepositoryException.class);

        assertThrows(MovieServiceReadException.class, () -> movieService.findAll());

        verify(readMoviePort, times(1)).findAll();
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

        verify(updateMoviePort).update(movieArgumentCaptor.capture());

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

        verify(updateMoviePort, times(1)).update(movieNo1);
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

        doThrow(MovieServiceUpdateException.class).when(updateMoviePort).update(movieArgumentCaptor.capture());

        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));

        verify(updateMoviePort, times(1)).update(movieNo1);
    }

    // Delete tests

    @Test
    public void movieServiceDeleteMovieTestPositive() throws MovieServiceReadException, MovieServiceDeleteException {
        UUID removedMovieUUID = movieNo1.getMovieID();

        when(readMoviePort.findByUUID(removedMovieUUID)).thenThrow(MovieServiceDeleteException.class);

        movieService.delete(removedMovieUUID);
        verify(deleteMoviePort).delete(uuidArgumentCaptor.capture());

        UUID capturedMovieUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedMovieUUID);
        assertEquals(removedMovieUUID, capturedMovieUUID);
        assertThrows(MovieServiceDeleteException.class, () -> movieService.findByUUID(removedMovieUUID));

        verify(deleteMoviePort, times(1)).delete(Mockito.eq(removedMovieUUID));
        verify(readMoviePort, times(1)).findByUUID(removedMovieUUID);
    }

    @Test
    public void movieServiceDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        UUID exampleUUID = UUID.randomUUID();

        doThrow(MovieRepositoryException.class).when(deleteMoviePort).delete(uuidArgumentCaptor.capture());

        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(exampleUUID));

        UUID capturedMovieUUID = uuidArgumentCaptor.getValue();
        assertEquals(exampleUUID, capturedMovieUUID);

        verify(deleteMoviePort, times(1)).delete(Mockito.eq(exampleUUID));
    }

    @Test
    public void movieServiceUpdateMovieWhenMovieRepositoryExceptionIsThrownTestNegative() {
        String errorMessage = "Repository exception";
        MongoException exception = null;

        MovieRepositoryException movieRepositoryException = new MovieRepositoryException(errorMessage, exception);

        doThrow(movieRepositoryException).when(updateMoviePort).update(any(Movie.class));

        MovieServiceUpdateException thrownException = assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));

        assertTrue(thrownException.getMessage().contains(errorMessage));

        verify(updateMoviePort, times(1)).update(any(Movie.class));
    }

}
