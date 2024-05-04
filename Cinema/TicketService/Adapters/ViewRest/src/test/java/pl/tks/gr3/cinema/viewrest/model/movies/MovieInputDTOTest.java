package pl.tks.gr3.cinema.viewrest.model.movies;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewrest.model.movies.MovieInputDTO;

import static org.junit.jupiter.api.Assertions.*;

public class MovieInputDTOTest {

    private static String movieTitle;
    private static double movieBasePrice;
    private static int scrRoomNumber;
    private static int numberOfAvailableSeats;

    private MovieInputDTO movieInputDTO;

    @BeforeAll
    public static void init() {
        movieTitle = "ExampleMovieTitleNo1";
        movieBasePrice = 40.25;
        scrRoomNumber = 2;
        numberOfAvailableSeats = 30;
    }

    @BeforeEach
    public void initializeMovieInputDTO() {
        movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
    }

    @Test
    public void movieInputDTONoArgsConstructorTestPositive() {
        MovieInputDTO testMovieInputDTO = new MovieInputDTO();
        assertNotNull(testMovieInputDTO);
    }

    @Test
    public void movieInputDTOAllArgsConstructorAndGettersTestPositive() {
        MovieInputDTO testMovieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(testMovieInputDTO);
        assertEquals(movieTitle, testMovieInputDTO.getMovieTitle());
        assertEquals(movieBasePrice, testMovieInputDTO.getMovieBasePrice());
        assertEquals(scrRoomNumber, testMovieInputDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, testMovieInputDTO.getNumberOfAvailableSeats());
    }

    @Test
    public void movieInputDTOMovieTitleSetterTestPositive() {
        String movieTitleBefore = movieInputDTO.getMovieTitle();
        assertNotNull(movieTitleBefore);
        String newMovieTitle = "NewMovieTitleNo1";
        assertNotNull(newMovieTitle);
        movieInputDTO.setMovieTitle(newMovieTitle);
        String movieTitleAfter = movieInputDTO.getMovieTitle();
        assertNotNull(movieTitleAfter);

        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    public void movieInputDTOMovieBasePriceSetterTestPositive() {
        double movieBasePriceBefore = movieInputDTO.getMovieBasePrice();
        double newMovieBasePrice = 27.23;
        movieInputDTO.setMovieBasePrice(newMovieBasePrice);
        double movieBasePriceAfter = movieInputDTO.getMovieBasePrice();

        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
    }

    @Test
    public void movieInputDTOScrRoomNumberSetterTestPositive() {
        int scrRoomNumberBefore = movieInputDTO.getScrRoomNumber();
        int newScrRoomNumber = 1;
        movieInputDTO.setScrRoomNumber(newScrRoomNumber);
        int scrRoomNumberAfter = movieInputDTO.getScrRoomNumber();

        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
    }

    @Test
    public void movieInputDTONumberOfAvailableSeatsSetterTestPositive() {
        int numberOfAvailableSeatsBefore = movieInputDTO.getNumberOfAvailableSeats();
        int newNumberOfAvailableSeats = 31;
        movieInputDTO.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        int numberOfAvailableSeatsAfter = movieInputDTO.getNumberOfAvailableSeats();

        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }
}