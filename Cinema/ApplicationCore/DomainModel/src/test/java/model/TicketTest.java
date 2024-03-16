package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.domain_model.model.Movie;
import pl.tks.gr3.cinema.domain_model.model.Ticket;
import pl.tks.gr3.cinema.domain_model.model.users.Client;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketTest {

    private static UUID ticketIDNo1;
    private static UUID ticketIDNo2;
    private static LocalDateTime movieTimeNo1;
    private static LocalDateTime movieTimeNo2;

    private static double ticketFinalPriceNo1;

    private static Client clientNo1;
    private static Client clientNo2;
    private static Movie movieNo1;
    private static Movie movieNo2;

    private static double movieBasePriceNo1;
    private static double movieBasePriceNo2;

    private static int numberOfAvailableSeatsNo1;
    private static int numberOfAvailableSeatsNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;

    @BeforeAll
    public static void initializeVariables() {
        ticketIDNo1 = UUID.randomUUID();
        ticketIDNo2 = UUID.randomUUID();

        movieTimeNo1 = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS);

        ticketFinalPriceNo1 = 50.00;

        clientNo1 = new Client(UUID.randomUUID(), "SomeExampleLogin", "SomeExamplePassword");
        clientNo2 = new Client(UUID.randomUUID(), "SomeExampleLogin", "SomeExamplePassword");

        movieBasePriceNo1 = 45.00;
        movieBasePriceNo2 = 47.50;

        numberOfAvailableSeatsNo1 = 45;
        numberOfAvailableSeatsNo2 = 60;

        movieNo1 = new Movie(UUID.randomUUID(), "SomeExampleTitle", movieBasePriceNo1, 1, numberOfAvailableSeatsNo1);
        movieNo2 = new Movie(UUID.randomUUID(), "SomeExampleTitle", movieBasePriceNo2, 2, numberOfAvailableSeatsNo2);
    }

    @BeforeEach
    public void initializeTickets()  {
        ticketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        ticketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, movieNo1.getMovieBasePrice(), clientNo2.getUserID(), movieNo2.getMovieID());
        ticketNo3 = new Ticket(ticketNo1.getTicketID(),
                ticketNo1.getMovieTime(),
                ticketNo1.getTicketPrice(),
                ticketNo1.getUserID(),
                ticketNo1.getMovieID());
    }

    @Test
    public void ticketModelLayerConstructorAndGettersTestPositive() {
        Ticket testTicketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(testTicketNo1);
        assertEquals(ticketIDNo1, testTicketNo1.getTicketID());
        assertEquals(movieTimeNo1, testTicketNo1.getMovieTime());
        assertEquals(clientNo1.getUserID(), testTicketNo1.getUserID());
        assertEquals(movieNo1.getMovieID(), testTicketNo1.getMovieID());
        assertEquals(testTicketNo1.getTicketPrice(), movieBasePriceNo1);

        Ticket testTicketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, movieNo2.getMovieBasePrice(), clientNo2.getUserID(), movieNo2.getMovieID());
        assertNotNull(testTicketNo2);
        assertEquals(ticketIDNo2, testTicketNo2.getTicketID());
        assertEquals(movieTimeNo2, testTicketNo2.getMovieTime());
        assertEquals(clientNo2.getUserID(), testTicketNo2.getUserID());
        assertEquals(movieNo2.getMovieID(), testTicketNo2.getMovieID());
        assertEquals(testTicketNo2.getTicketPrice(), movieBasePriceNo2);
    }

    @Test
    public void ticketDataLayerConstructorAndGettersTestPositive() {
        Ticket testTicket = new Ticket(ticketIDNo1, movieTimeNo1, ticketFinalPriceNo1, clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(testTicket);
        assertEquals(ticketIDNo1, testTicket.getTicketID());
        assertEquals(movieTimeNo1, testTicket.getMovieTime());
        assertEquals(ticketFinalPriceNo1, testTicket.getTicketPrice());
        assertEquals(clientNo1.getUserID(), testTicket.getUserID());
        assertEquals(movieNo1.getMovieID(), testTicket.getMovieID());
    }

    @Test
    public void ticketMovieTimeSetterTestPositive() {
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        assertNotNull(movieTimeBefore);
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(4).plusHours(1);
        assertNotNull(newMovieTime);
        ticketNo1.setMovieTime(newMovieTime);
        LocalDateTime movieTimeAfter = ticketNo1.getMovieTime();
        assertNotNull(movieTimeAfter);
        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketEqualsMethodWithItselfTestPositive() {
        boolean equalsResult = ticketNo1.equals(ticketNo1);
        assertTrue(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithNullTestNegative() {
        boolean equalsResult = ticketNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithObjectOfDifferentClassTestNegative() {
        boolean equalsResult = ticketNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean equalsResult = ticketNo1.equals(ticketNo2);
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithObjectOfTheSameClassAndTheSameTestPositive() {
        boolean equalsResult = ticketNo1.equals(ticketNo3);
        assertTrue(equalsResult);
    }

    @Test
    public void ticketHashCodeTestPositive() {
        int hashCodeFromTicketNo1 = ticketNo1.hashCode();
        int hashCodeFromTicketNo3 = ticketNo3.hashCode();
        assertEquals(hashCodeFromTicketNo1, hashCodeFromTicketNo3);
        assertEquals(ticketNo1, ticketNo3);
    }

    @Test
    public void ticketHashCodeTestNegative() {
        int hashCodeFromTicketNo1 = ticketNo1.hashCode();
        int hashCodeFromTicketNo2 = ticketNo2.hashCode();
        assertNotEquals(hashCodeFromTicketNo1, hashCodeFromTicketNo2);
    }

    @Test
    public void ticketToStringTestPositive() {
        String ticketToStringResult = ticketNo1.toString();
        assertNotNull(ticketToStringResult);
        assertFalse(ticketToStringResult.isEmpty());
    }
}