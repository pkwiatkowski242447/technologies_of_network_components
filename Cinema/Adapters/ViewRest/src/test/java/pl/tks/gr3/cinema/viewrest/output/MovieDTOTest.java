package pl.tks.gr3.cinema.viewrest.output;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovieDTOTest {

    private static UUID uuidNo1;
    private static String movieTitleNo1;
    private static double movieBasePriceNo1;
    private static int scrRoomNumberNo1;
    private static int numberOfAvailableSeatsNo1;

    private MovieDTO movieDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        movieTitleNo1 = "Example Movie Title No. 1";
        movieBasePriceNo1 = 45.50;
        scrRoomNumberNo1 = 5;
        numberOfAvailableSeatsNo1 = 60;
    }

    @BeforeEach
    public void initializeMovieDTO() {
        movieDTO = new MovieDTO(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
    }

    @Test
    public void movieDTONoArgsConstructorTestPositive() {
        MovieDTO testMovieDTO = new MovieDTO();
        assertNotNull(testMovieDTO);
    }

    @Test
    public void movieDTOAllArgsConstructorAndGettersTestPositive() {
        MovieDTO testMovieDTO = new MovieDTO(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
        assertNotNull(testMovieDTO);
        assertEquals(uuidNo1, testMovieDTO.getMovieID());
        assertEquals(movieTitleNo1, testMovieDTO.getMovieTitle());
        assertEquals(movieBasePriceNo1, testMovieDTO.getMovieBasePrice());
        assertEquals(scrRoomNumberNo1, testMovieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeatsNo1, testMovieDTO.getNumberOfAvailableSeats());
    }

    @Test
    public void movieDTOMovieIDSetterTestPositive() {
        UUID movieDTOIDBefore = movieDTO.getMovieID();
        assertNotNull(movieDTOIDBefore);
        UUID newMovieDTOID = UUID.randomUUID();
        assertNotNull(newMovieDTOID);
        assertNotEquals(newMovieDTOID, movieDTOIDBefore);
        movieDTO.setMovieID(newMovieDTOID);
        UUID movieDTOIDAfter = movieDTO.getMovieID();
        assertNotNull(movieDTOIDAfter);
        assertEquals(newMovieDTOID, movieDTOIDAfter);
        assertNotEquals(movieDTOIDBefore, movieDTOIDAfter);
    }

    @Test
    public void movieDTOMovieTitleSetterTestPositive() {
        String movieDTOMovieTitleBefore = movieDTO.getMovieTitle();
        assertNotNull(movieDTOMovieTitleBefore);
        String newMovieDTOMovieTitle = "New Movie DTO Movie Title No.1";
        assertNotNull(newMovieDTOMovieTitle);
        assertNotEquals(newMovieDTOMovieTitle, movieDTOMovieTitleBefore);
        movieDTO.setMovieTitle(newMovieDTOMovieTitle);
        String movieDTOMovieTitleAfter = movieDTO.getMovieTitle();
        assertNotNull(movieDTOMovieTitleAfter);
        assertEquals(newMovieDTOMovieTitle, movieDTOMovieTitleAfter);
        assertNotEquals(movieDTOMovieTitleBefore, movieDTOMovieTitleAfter);
    }

    @Test
    public void movieDTOMovieBasePriceSetterTestPositive() {
        double movieDTOMovieBasePriceBefore = movieDTO.getMovieBasePrice();
        double newMovieDTOMovieBasePrice = 30.89;
        assertNotEquals(newMovieDTOMovieBasePrice, movieDTOMovieBasePriceBefore);
        movieDTO.setMovieBasePrice(newMovieDTOMovieBasePrice);
        double movieDTOMovieBasePriceAfter = movieDTO.getMovieBasePrice();
        assertEquals(newMovieDTOMovieBasePrice, movieDTOMovieBasePriceAfter);
        assertNotEquals(movieDTOMovieBasePriceBefore, movieDTOMovieBasePriceAfter);
    }

    @Test
    public void movieDTOScrRoomNumberSetterTestPositive() {
        int movieDTOScrRoomNumberBefore = movieDTO.getScrRoomNumber();
        int newMovieDTOScrRoomNumber = 12;
        assertNotEquals(newMovieDTOScrRoomNumber, movieDTOScrRoomNumberBefore);
        movieDTO.setScrRoomNumber(newMovieDTOScrRoomNumber);
        int movieDTOScrRoomNumberAfter = movieDTO.getScrRoomNumber();
        assertEquals(newMovieDTOScrRoomNumber, movieDTOScrRoomNumberAfter);
        assertNotEquals(movieDTOScrRoomNumberBefore, movieDTOScrRoomNumberAfter);
    }

    @Test
    public void movieDTONumberOfAvailableSeatsSetterTestPositive() {
        int movieDTONumberOfAvailableSeatsBefore = movieDTO.getNumberOfAvailableSeats();
        int newMovieDTONumberOfAvailableSeats = 12;
        assertNotEquals(newMovieDTONumberOfAvailableSeats, movieDTONumberOfAvailableSeatsBefore);
        movieDTO.setNumberOfAvailableSeats(newMovieDTONumberOfAvailableSeats);
        int movieDTONumberOfAvailableSeatsAfter = movieDTO.getNumberOfAvailableSeats();
        assertEquals(newMovieDTONumberOfAvailableSeats, movieDTONumberOfAvailableSeatsAfter);
        assertNotEquals(movieDTONumberOfAvailableSeatsBefore, movieDTONumberOfAvailableSeatsAfter);
    }
}
