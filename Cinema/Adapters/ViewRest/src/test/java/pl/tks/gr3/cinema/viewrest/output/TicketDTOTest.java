package pl.tks.gr3.cinema.viewrest.output;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDTOTest {

    private static UUID uuidNo1;
    private static LocalDateTime movieTimeNo1;
    private static double ticketFinalPriceNo1;
    private static UUID clientID;
    private static UUID movieID;

    private TicketDTO ticketDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        movieTimeNo1 = LocalDateTime.now().plusDays(3).plusHours(3).truncatedTo(ChronoUnit.SECONDS);
        ticketFinalPriceNo1 = 50.75;
        clientID = UUID.randomUUID();
        movieID = UUID.randomUUID();
    }

    @BeforeEach
    public void initializeTicketDTO() {
        ticketDTO = new TicketDTO(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientID, movieID);
    }

    @Test
    public void ticketDTONoArgsConstructorTestPositive() {
        TicketDTO testTicketDTO = new TicketDTO();
        assertNotNull(testTicketDTO);
    }

    @Test
    public void ticketDTOAllArgsConstructorAndGettersTestPositive() {
        TicketDTO testTicketDTO = new TicketDTO(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientID, movieID);
        assertNotNull(testTicketDTO);
        assertEquals(uuidNo1, testTicketDTO.getTicketID());
        assertEquals(movieTimeNo1, testTicketDTO.getMovieTime());
        assertEquals(ticketFinalPriceNo1, testTicketDTO.getTicketFinalPrice());
        assertEquals(clientID, testTicketDTO.getClientID());
        assertEquals(movieID, testTicketDTO.getMovieID());
    }

    @Test
    public void ticketDTOIDSetterTestPositive() {
        UUID ticketDTOTicketIDBefore = ticketDTO.getTicketID();
        assertNotNull(ticketDTOTicketIDBefore);
        UUID newTicketDTOTicketID = UUID.randomUUID();
        assertNotNull(newTicketDTOTicketID);
        ticketDTO.setTicketID(newTicketDTOTicketID);
        UUID ticketDTOTicketIDAfter = ticketDTO.getTicketID();
        assertNotNull(ticketDTOTicketIDAfter);
        assertEquals(newTicketDTOTicketID, ticketDTOTicketIDAfter);
        assertNotEquals(ticketDTOTicketIDBefore, ticketDTOTicketIDAfter);
    }

    @Test
    public void ticketDTOMovieTimeSetterTestPositive() {
        LocalDateTime ticketDTOMovieTimeBefore = ticketDTO.getMovieTime();
        assertNotNull(ticketDTOMovieTimeBefore);
        LocalDateTime newTicketDTOMovieTime = LocalDateTime.now().plusDays(1).plusHours(12).truncatedTo(ChronoUnit.SECONDS);
        assertNotNull(newTicketDTOMovieTime);
        ticketDTO.setMovieTime(newTicketDTOMovieTime);
        LocalDateTime ticketDTOMovieTimeAfter = ticketDTO.getMovieTime();
        assertNotNull(ticketDTOMovieTimeAfter);
        assertEquals(newTicketDTOMovieTime, ticketDTOMovieTimeAfter);
        assertNotEquals(ticketDTOMovieTimeBefore, ticketDTOMovieTimeAfter);
    }

    @Test
    public void ticketDTOTicketFinalPriceSetterTestPositive() {
        double ticketDTOTicketFinalPriceBefore = ticketDTO.getTicketFinalPrice();
        double newTicketDTOTicketFinalPrice = 75.20;
        ticketDTO.setTicketFinalPrice(newTicketDTOTicketFinalPrice);
        double ticketDTOTicketFinalPriceAfter = ticketDTO.getTicketFinalPrice();
        assertEquals(newTicketDTOTicketFinalPrice, ticketDTOTicketFinalPriceAfter);
        assertNotEquals(ticketDTOTicketFinalPriceBefore, ticketDTOTicketFinalPriceAfter);
    }

    @Test
    public void ticketDTOClientIDSetterTestPositive() {
        UUID ticketDTOClientIDBefore = ticketDTO.getClientID();
        assertNotNull(ticketDTOClientIDBefore);
        UUID newTicketDTOClientID = UUID.randomUUID();
        assertNotNull(newTicketDTOClientID);
        ticketDTO.setClientID(newTicketDTOClientID);
        UUID ticketDTOClientIDAfter = ticketDTO.getClientID();
        assertNotNull(ticketDTOClientIDAfter);
        assertEquals(newTicketDTOClientID, ticketDTOClientIDAfter);
        assertNotEquals(ticketDTOClientIDBefore, ticketDTOClientIDAfter);
    }

    @Test
    public void ticketDTOMovieIDSetterTestPositive() {
        UUID ticketDTOMovieIDBefore = ticketDTO.getMovieID();
        assertNotNull(ticketDTOMovieIDBefore);
        UUID newTicketDTOMovieID = UUID.randomUUID();
        assertNotNull(newTicketDTOMovieID);
        ticketDTO.setMovieID(newTicketDTOMovieID);
        UUID ticketDTOMovieIDAfter = ticketDTO.getMovieID();
        assertNotNull(ticketDTOMovieIDAfter);
        assertEquals(newTicketDTOMovieID, ticketDTOMovieIDAfter);
        assertNotEquals(ticketDTOMovieIDBefore, ticketDTOMovieIDAfter);
    }
}