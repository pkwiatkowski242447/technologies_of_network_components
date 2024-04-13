package pl.tks.gr3.cinema.adapters.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.adapters.model.MovieEnt;
import pl.tks.gr3.cinema.domain_model.Movie;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MovieConverterTest {

    private MovieEnt movieEntNo1;
    private Movie movieNo1;


    @BeforeEach
    public void setUpBeforeEachMethod() {
        movieEntNo1 = new MovieEnt(UUID.randomUUID(), "UniqueMovieEntNameNo1", 10, 1, 10);

        movieNo1 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo1", 20, 2, 20);
    }

    @SuppressWarnings({"InstantiationOfUtilityClass"})
    @Test
    public void movieConverterNoArgsConstructorTestPositive() {
        MovieConverter movieConverter = new MovieConverter();
        assertNotNull(movieConverter);
    }

    @Test
    public void movieConverterConvertMovieToMovieEntTestPositive() {
        MovieEnt convertedMovieEnt = MovieConverter.convertToMovieEnt(movieNo1);

        assertNotNull(convertedMovieEnt);
        assertEquals(movieNo1.getMovieID(), convertedMovieEnt.getMovieID());
        assertEquals(movieNo1.getMovieTitle(), convertedMovieEnt.getMovieTitle());
        assertEquals(movieNo1.getMovieBasePrice(), convertedMovieEnt.getMovieBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), convertedMovieEnt.getScrRoomNumber());
        assertEquals(movieNo1.getNumberOfAvailableSeats(), convertedMovieEnt.getNumberOfAvailableSeats());
    }

    @Test
    public void movieConverterConvertMovieEntToMovieTestPositive() {
        Movie convertedMovie = MovieConverter.convertToMovie(movieEntNo1);

        assertNotNull(convertedMovie);
        assertEquals(movieEntNo1.getMovieID(), convertedMovie.getMovieID());
        assertEquals(movieEntNo1.getMovieTitle(), convertedMovie.getMovieTitle());
        assertEquals(movieEntNo1.getMovieBasePrice(), convertedMovie.getMovieBasePrice());
        assertEquals(movieEntNo1.getScrRoomNumber(), convertedMovie.getScrRoomNumber());
        assertEquals(movieEntNo1.getNumberOfAvailableSeats(), convertedMovie.getNumberOfAvailableSeats());
    }
}
