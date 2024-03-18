package aggregates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.tks.gr3.cinema.adapters.aggregates.MovieRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.MovieRepositoryException;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.adapters.model.TicketEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.repositories.MovieRepository;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieRepositoryAdapterTest {
    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieRepositoryAdapter movieRepositoryAdapter;

    @Captor
    private static ArgumentCaptor<MovieEnt> movieArgumentCaptor;

    @Captor
    private static ArgumentCaptor<UUID> uuidArgumentCaptor;

    private static MovieEnt movieEntNo1;
    private static MovieEnt movieEntNo2;
    private static MovieEnt movieEntNo3;

    private static ClientEnt clientEntNo1;
    private static ClientEnt clientEntNo2;

    private static TicketEnt ticketEntNo1;
    private static TicketEnt ticketEntNo2;
    private static TicketEnt ticketEntNo3;

    @BeforeEach
    public void initializeSampleData() {
        movieEntNo1 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo1", 10.50, 1, 30);
        movieEntNo2 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo2", 23.75, 2, 45);
        movieEntNo3 = new MovieEnt(UUID.randomUUID(), "ExampleTitleNo3", 40.25, 3, 60);

        clientEntNo1 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo1", "ClientPasswordNo1");
        clientEntNo2 = new ClientEnt(UUID.randomUUID(), "ClientLoginNo2", "ClientPasswordNo2");

        LocalDateTime localDateTimeNo1 = LocalDateTime.of(2023, 11, 2, 20, 15, 0);
        LocalDateTime localDateTimeNo2 = LocalDateTime.of(2023, 10, 28, 18, 45, 0);

        ticketEntNo1 = new TicketEnt(UUID.randomUUID(), localDateTimeNo1, 12.5, clientEntNo1.getUserID() , movieEntNo1.getMovieID());
        ticketEntNo2 = new TicketEnt(UUID.randomUUID(), localDateTimeNo2, 13.5, clientEntNo2.getUserID(), movieEntNo2.getMovieID());
        ticketEntNo3 = new TicketEnt(UUID.randomUUID(), localDateTimeNo2, 13.5, clientEntNo1.getUserID() , movieEntNo2.getMovieID());
    }

    @Test
    public void movieRepositoryAdapterCreateMovieTestPositive() throws MovieRepositoryException {
        when(movieRepository.create(Mockito.eq(movieEntNo1.getMovieTitle()), Mockito.eq(movieEntNo1.getMovieBasePrice()), Mockito.eq(movieEntNo1.getScrRoomNumber()), Mockito.eq(movieEntNo1.getNumberOfAvailableSeats()))).thenReturn(movieEntNo1);
        Movie movie = movieRepositoryAdapter.create(movieEntNo1.getMovieTitle(), movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());

        assertNotNull(movie);
        assertEquals(movieEntNo1.getMovieTitle(), movie.getMovieTitle());
        assertEquals(movieEntNo1.getMovieBasePrice(), movie.getMovieBasePrice());
        assertEquals(movieEntNo1.getScrRoomNumber(), movie.getScrRoomNumber());
        assertEquals(movieEntNo1.getNumberOfAvailableSeats(), movie.getNumberOfAvailableSeats());
        assertEquals(movieEntNo1.getMovieID(), movie.getMovieID());
        verify(movieRepository, times(1)).create(movieEntNo1.getMovieTitle(), movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieRepositoryAdapterCreateWithNullMovieTitleTestNegative() throws MovieRepositoryException {
        when(movieRepository.create(Mockito.isNull(), Mockito.eq(movieEntNo1.getMovieBasePrice()), Mockito.eq(movieEntNo1.getScrRoomNumber()), Mockito.eq(movieEntNo1.getNumberOfAvailableSeats()))).thenThrow(MovieRepositoryException.class);
        assertThrows(MovieRepositoryException.class, () -> movieRepositoryAdapter.create(null, movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats()));
        verify(movieRepository, times(1)).create(null, movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieRepositoryAdapterFindAllTestPositive() {
        List<MovieEnt> listOfAllMovies = new ArrayList<>();
        listOfAllMovies.add(movieEntNo1);
        listOfAllMovies.add(movieEntNo2);
        listOfAllMovies.add(movieEntNo3);

        when(movieRepository.findAll()).thenReturn(listOfAllMovies);
        List<Movie> movies = movieRepositoryAdapter.findAll();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertEquals(3, movies.size());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    public void movieRepositoryAdapterFindByUUIDTestPositive() {
        when(movieRepository.findByUUID(Mockito.eq(movieEntNo1.getMovieID()))).thenReturn(movieEntNo1);
        Movie movie = movieRepositoryAdapter.findByUUID(movieEntNo1.getMovieID());
        assertNotNull(movie);
        assertEquals(movieEntNo1.getMovieTitle(), movie.getMovieTitle());
        assertEquals(movieEntNo1.getMovieBasePrice(), movie.getMovieBasePrice());
        assertEquals(movieEntNo1.getScrRoomNumber(), movie.getScrRoomNumber());
        assertEquals(movieEntNo1.getNumberOfAvailableSeats(), movie.getNumberOfAvailableSeats());
        assertEquals(movieEntNo1.getMovieID(), movie.getMovieID());
        verify(movieRepository, times(1)).findByUUID(movieEntNo1.getMovieID());
    }

    @Test
    public void movieRepositoryAdapterFindByUUIDTestNegative() {
        when(movieRepository.findByUUID(Mockito.eq(movieEntNo2.getMovieID()))).thenThrow(MovieRepositoryException.class);
        assertThrows(MovieRepositoryException.class, () -> movieRepositoryAdapter.findByUUID(movieEntNo2.getMovieID()));
        verify(movieRepository, times(1)).findByUUID(movieEntNo2.getMovieID());
    }

    @Test
    public void movieRepositoryAdapterGetListOfTicketsTestPositive() {
        List<TicketEnt> ticketsList = new ArrayList<>();
        ticketsList.add(ticketEntNo1);
        ticketsList.add(ticketEntNo3);
        when(movieRepository.getListOfTicketsForMovie(movieEntNo1.getMovieID())).thenReturn(ticketsList);
        List<Ticket> tickets = movieRepositoryAdapter.getListOfTickets(movieEntNo1.getMovieID());
        assertNotNull(tickets);
        assertFalse(tickets.isEmpty());
        assertEquals(2, tickets.size());
    }

    @Test
    public void movieRepositoryAdapterUpdateTestPositive() {
        String newTitle = "NewMovieTitle";

        Movie movie = new Movie(movieEntNo1.getMovieID(), newTitle, movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());
        movieRepositoryAdapter.update(movie);
        verify(movieRepository).update(movieArgumentCaptor.capture());
        MovieEnt captureMovie = movieArgumentCaptor.getValue();

        String titleAfter = captureMovie.getMovieTitle();

        assertNotNull(titleAfter);
        assertEquals(newTitle, titleAfter);
        verify(movieRepository, times(1)).update(movieArgumentCaptor.capture());
    }

    @Test
    public void movieRepositoryAdapterUpdateTestNegative() {
        Movie movie = new Movie(movieEntNo1.getMovieID(), movieEntNo1.getMovieTitle(), movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());

        doThrow(MovieRepositoryException.class).when(movieRepository).update(movieArgumentCaptor.capture());

        assertThrows(MovieRepositoryException.class, () -> movieRepositoryAdapter.update(movie));
        verify(movieRepository, times(1)).update(movieArgumentCaptor.capture());
    }

    @Test
    public void movieRepositoryAdapterDeleteTestPositive() {
        UUID removedMovieUUID = movieEntNo1.getMovieID();
        Movie movie = new Movie(removedMovieUUID, movieEntNo1.getMovieTitle(), movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());
        movieRepositoryAdapter.delete(removedMovieUUID);
        verify(movieRepository).delete(uuidArgumentCaptor.capture());
        UUID capturedMovieUUID = uuidArgumentCaptor.getValue();

        assertNotNull(capturedMovieUUID);
        assertEquals(removedMovieUUID, capturedMovieUUID);

        verify(movieRepository, times(1)).delete(removedMovieUUID);
    }

    @Test
    public void movieRepositoryAdapterDeleteTestNegative() {
        UUID removedMovieUUID = movieEntNo1.getMovieID();
        Movie movie = new Movie(removedMovieUUID, movieEntNo1.getMovieTitle(), movieEntNo1.getMovieBasePrice(), movieEntNo1.getScrRoomNumber(), movieEntNo1.getNumberOfAvailableSeats());
        doThrow(MovieRepositoryException.class).when(movieRepository).delete(removedMovieUUID);

        assertThrows(MovieRepositoryException.class, () -> movieRepositoryAdapter.delete(removedMovieUUID));

        verify(movieRepository, times(1)).delete(removedMovieUUID);
    }
}
