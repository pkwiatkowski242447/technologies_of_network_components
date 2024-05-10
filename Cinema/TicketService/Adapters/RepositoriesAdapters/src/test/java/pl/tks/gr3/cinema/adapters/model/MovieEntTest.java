package pl.tks.gr3.cinema.adapters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovieEntTest {

    private MovieEnt movieEntNo1;
    private MovieEnt movieEntNo2;
    private MovieEnt movieEntNo3;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        movieEntNo1 = new MovieEnt(UUID.randomUUID(), "UniqueMovieTitleNo1", 45.00, 1, 50);
        movieEntNo2 = new MovieEnt(UUID.randomUUID(), "UniqueMovieTitleNo2", 25.00, 2, 25);
        movieEntNo3 = new MovieEnt(movieEntNo1.getMovieID(),
                movieEntNo1.getMovieTitle(),
                movieEntNo1.getMovieBasePrice(),
                movieEntNo1.getScrRoomNumber(),
                movieEntNo1.getNumberOfAvailableSeats());
    }

    @Test
    public void movieEntAllArgsConstructorAndGettersTestPositive() {
        MovieEnt movieEnt = new MovieEnt(movieEntNo1.getMovieID(),
                movieEntNo1.getMovieTitle(),
                movieEntNo1.getMovieBasePrice(),
                movieEntNo1.getScrRoomNumber(),
                movieEntNo1.getNumberOfAvailableSeats());

        assertNotNull(movieEnt);
        assertEquals(movieEntNo1.getMovieID(), movieEnt.getMovieID());
        assertEquals(movieEntNo1.getMovieTitle(), movieEnt.getMovieTitle());
        assertEquals(movieEntNo1.getMovieBasePrice(), movieEnt.getMovieBasePrice());
        assertEquals(movieEntNo1.getScrRoomNumber(), movieEnt.getScrRoomNumber());
        assertEquals(movieEntNo1.getNumberOfAvailableSeats(), movieEnt.getNumberOfAvailableSeats());
    }

    @Test
    public void movieEntSetMovieTitleTestPositive() {
        String movieTitleBefore = movieEntNo1.getMovieTitle();
        assertNotNull(movieTitleBefore);

        String newMovieTitle = "NewMovieTitleNo1";
        assertNotNull(newMovieTitle);

        movieEntNo1.setMovieTitle(newMovieTitle);

        String movieTitleAfter = movieEntNo1.getMovieTitle();

        assertNotNull(movieTitleAfter);
        assertFalse(movieTitleAfter.isEmpty());
        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    public void movieEntSetMovieBasePriceTestPositive() {
        double movieBasePriceBefore = movieEntNo1.getMovieBasePrice();
        double newMovieBasePrice = 50.00;

        movieEntNo1.setMovieBasePrice(newMovieBasePrice);

        double movieBasePriceAfter = movieEntNo1.getMovieBasePrice();

        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
    }

    @Test
    public void movieEntSetScrRoomNumberTestPositive() {
        int scrRoomNumberBefore = movieEntNo1.getScrRoomNumber();
        int newScrRoomNumber = 10;

        movieEntNo1.setScrRoomNumber(newScrRoomNumber);

        int scrRoomNumberAfter = movieEntNo1.getScrRoomNumber();

        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
    }

    @Test
    public void movieEntSetNumberOfAvailableSeatsTestPositive() {
        int numberOfAvailableSeatsBefore = movieEntNo1.getNumberOfAvailableSeats();
        int newNumberOfAvailableSeats = 20;

        movieEntNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);

        int numberOfAvailableSeatsAfter = movieEntNo1.getNumberOfAvailableSeats();

        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }

    @Test
    public void movieEntEqualsWithItselfTestPositive() {
        boolean result = movieEntNo1.equals(movieEntNo1);
        assertTrue(result);
    }

    @Test
    public void movieEntEqualsWithNullTestNegative() {
        boolean result = movieEntNo1.equals(null);
        assertFalse(result);
    }

    @Test
    public void movieEntEqualsWithObjectOfDifferentClassTestNegative() {
        boolean result = movieEntNo1.equals(new Object());
        assertFalse(result);
    }

    @Test
    public void movieEntEqualsWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean result = movieEntNo1.equals(movieEntNo2);
        assertFalse(result);
    }

    @Test
    public void movieEntEqualsWithTheSameObjectTestPositive() {
        boolean result = movieEntNo1.equals(movieEntNo3);
        assertTrue(result);
    }

    @Test
    public void movieEntHashCodeTestPositive() {
        int hashCodeNo1 = movieEntNo1.hashCode();
        int hashCodeNo2 = movieEntNo3.hashCode();

        assertEquals(movieEntNo1, movieEntNo3);
        assertEquals(hashCodeNo1, hashCodeNo2);
    }

    @Test
    public void movieEntHashCodeTestNegative() {
        int hashCodeNo1 = movieEntNo1.hashCode();
        int hashCodeNo2 = movieEntNo2.hashCode();

        assertNotEquals(hashCodeNo1, hashCodeNo2);
        assertNotEquals(movieEntNo1, movieEntNo2);
    }

    @Test
    public void movieEntToStringTestPositive() {
        String toStringResult = movieEntNo1.toString();

        assertNotNull(toStringResult);
        assertFalse(toStringResult.isEmpty());
    }
}
