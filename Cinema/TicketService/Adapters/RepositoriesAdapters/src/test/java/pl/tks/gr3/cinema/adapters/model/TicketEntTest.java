package pl.tks.gr3.cinema.adapters.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketEntTest {

    private TicketEnt ticketEntNo1;
    private TicketEnt ticketEntNo2;
    private TicketEnt ticketEntNo3;

    @BeforeEach
    public void setUpBeforeEachMethod() {
        UUID clientID = UUID.randomUUID();
        UUID movieID = UUID.randomUUID();

        ticketEntNo1 = new TicketEnt(UUID.randomUUID(), LocalDateTime.now(), 45.00, clientID, movieID);
        ticketEntNo2 = new TicketEnt(UUID.randomUUID(), LocalDateTime.now(), 25.00, clientID, movieID);
        ticketEntNo3 = new TicketEnt(ticketEntNo1.getTicketID(),
                ticketEntNo1.getMovieTime(),
                ticketEntNo1.getTicketPrice(),
                ticketEntNo1.getUserID(),
                ticketEntNo1.getMovieID());
    }

    @Test
    public void ticketEntAllArgsConstructorAndGettersTestPositive() {
        TicketEnt createdTicketEnt = new TicketEnt(ticketEntNo1.getTicketID(),
                ticketEntNo1.getMovieTime(),
                ticketEntNo1.getTicketPrice(),
                ticketEntNo1.getUserID(),
                ticketEntNo1.getMovieID());

        assertNotNull(createdTicketEnt);
        assertEquals(ticketEntNo1.getTicketID(), createdTicketEnt.getTicketID());
        assertEquals(ticketEntNo1.getMovieTime(), createdTicketEnt.getMovieTime());
        assertEquals(ticketEntNo1.getTicketPrice(), createdTicketEnt.getTicketPrice());
        assertEquals(ticketEntNo1.getUserID(), createdTicketEnt.getUserID());
        assertEquals(ticketEntNo1.getMovieID(), createdTicketEnt.getMovieID());
    }

    @Test
    public void ticketEntSetMovieTimeTestPositive() {
        LocalDateTime movieTimeBefore = ticketEntNo1.getMovieTime();
        assertNotNull(movieTimeBefore);

        LocalDateTime newMovieTime = LocalDateTime.now().plusMinutes(15);
        assertNotNull(newMovieTime);

        ticketEntNo1.setMovieTime(newMovieTime);

        LocalDateTime movieTimeAfter = ticketEntNo1.getMovieTime();
        assertNotNull(movieTimeAfter);

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketEntEqualsMethodWithItselfTestPositive() {
        boolean equalsResult = ticketEntNo1.equals(ticketEntNo1);
        assertTrue(equalsResult);
    }

    @Test
    public void ticketEntEqualsMethodWithNullTestNegative() {
        boolean equalsResult = ticketEntNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEntEqualsMethodWithObjectOfDifferentClassTestNegative() {
        boolean equalsResult = ticketEntNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEntEqualsMethodWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean equalsResult = ticketEntNo1.equals(ticketEntNo2);
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEntEqualsMethodWithTheSameObjectTestPositive() {
        boolean equalsResult = ticketEntNo1.equals(ticketEntNo3);
        assertTrue(equalsResult);
    }

    @Test
    public void ticketEntHashCodeTestPositive() {
        int hashCodeResultNo1 = ticketEntNo1.hashCode();
        int hashCodeResultNo2 = ticketEntNo3.hashCode();

        assertEquals(ticketEntNo1, ticketEntNo3);
        assertEquals(hashCodeResultNo1, hashCodeResultNo2);
    }

    @Test
    public void ticketEntHashCodeTestNegative() {
        int hashCodeResultNo1 = ticketEntNo1.hashCode();
        int hashCodeResultNo2 = ticketEntNo2.hashCode();

        assertNotEquals(hashCodeResultNo1, hashCodeResultNo2);
        assertNotEquals(ticketEntNo1, ticketEntNo2);
    }

    @Test
    public void ticketEntToStringTestPositive() {
        String ticketEntString = ticketEntNo1.toString();

        assertNotNull(ticketEntString);
        assertFalse(ticketEntString.isEmpty());
    }
}
