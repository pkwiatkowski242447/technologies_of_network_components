package pl.tks.gr3.cinema.viewrest.input;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketInputDTOTest {

    private static String movieTime;
    private static UUID clientID;
    private static UUID movieID;
    private static String ticketType;

    private TicketInputDTO ticketInputDTO;

    @BeforeAll
    public static void init() {
        movieTime = LocalDateTime.now().plusDays(5).plusHours(5).truncatedTo(ChronoUnit.SECONDS).toString();
        clientID = UUID.randomUUID();
        movieID = UUID.randomUUID();
        ticketType = "normal";
    }

    @BeforeEach
    public void initializeTicketInputDTO() {
        ticketInputDTO = new TicketInputDTO(movieTime, clientID, movieID);
    }

    @Test
    public void ticketInputDTONoArgsConstructorTestPositive() {
        TicketInputDTO testTicketInputDTO = new TicketInputDTO();
        assertNotNull(testTicketInputDTO);
    }

    @Test
    public void ticketInputDTOAllArgsConstructorAndGettersTestPositive() {
        TicketInputDTO testTicketInputDTO = new TicketInputDTO(movieTime, clientID, movieID);
        assertNotNull(testTicketInputDTO);
    }

    @Test
    public void ticketInputDTOMovieTimeSetterTestPositive() {
        String movieTimeBefore = ticketInputDTO.getMovieTime();
        assertNotNull(movieTimeBefore);
        String newMovieTime = LocalDateTime.now().plusDays(2).plusDays(4).truncatedTo(ChronoUnit.SECONDS).toString();
        assertNotNull(newMovieTime);
        ticketInputDTO.setMovieTime(newMovieTime);
        String movieTimeAfter = ticketInputDTO.getMovieTime();
        assertNotNull(movieTimeAfter);

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketInputDTOClientIDSetterTestPositive() {
        UUID clientIDBefore = ticketInputDTO.getClientID();
        assertNotNull(clientIDBefore);
        UUID newClientID = UUID.randomUUID();
        assertNotNull(newClientID);
        ticketInputDTO.setClientID(newClientID);
        UUID clientIDAfter = ticketInputDTO.getClientID();
        assertNotNull(clientIDAfter);

        assertEquals(newClientID, clientIDAfter);
        assertNotEquals(clientIDBefore, clientIDAfter);
    }

    @Test
    public void ticketInputDTOMovieIDSetterTestPositive() {
        UUID movieIDBefore = ticketInputDTO.getMovieID();
        assertNotNull(movieIDBefore);
        UUID newMovieID = UUID.randomUUID();
        assertNotNull(newMovieID);
        ticketInputDTO.setMovieID(newMovieID);
        UUID movieIDAfter = ticketInputDTO.getMovieID();
        assertNotNull(movieIDAfter);

        assertEquals(newMovieID, movieIDAfter);
        assertNotEquals(movieIDBefore, movieIDAfter);
    }
}