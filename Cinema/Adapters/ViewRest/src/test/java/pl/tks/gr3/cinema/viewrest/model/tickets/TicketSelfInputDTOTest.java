package pl.tks.gr3.cinema.viewrest.model.tickets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketSelfInputDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketSelfInputDTOTest {

    private static String movieTime;
    private static UUID movieID;

    private TicketSelfInputDTO ticketSelfInputDTO;

    @BeforeEach
    public void initializeTicketSelfInputDTO() {
        movieTime = LocalDateTime.now().plusDays(5).plusHours(5).truncatedTo(ChronoUnit.SECONDS).toString();
        movieID = UUID.randomUUID();
        ticketSelfInputDTO = new TicketSelfInputDTO(movieTime, movieID);
    }

    @Test
    public void ticketSelfInputDTONoArgsConstructorTestPositive() {
        TicketSelfInputDTO testTicketSelfInputDTO = new TicketSelfInputDTO();
        assertNotNull(testTicketSelfInputDTO);
    }

    @Test
    public void ticketSelfInputDTOAllArgsConstructorAndGettersTestPositive() {
        TicketSelfInputDTO testTicketSelfInputDTO = new TicketSelfInputDTO(movieTime, movieID);
        assertNotNull(testTicketSelfInputDTO);
        assertEquals(movieTime, testTicketSelfInputDTO.getMovieTime());
        assertEquals(movieID, testTicketSelfInputDTO.getMovieID());
    }

    @Test
    public void ticketSelfInputDTOMovieTimeSetterTestPositive() {
        String movieTimeBefore = ticketSelfInputDTO.getMovieTime();
        assertNotNull(movieTimeBefore);
        String newMovieTime = LocalDateTime.now().plusDays(6).plusHours(6).truncatedTo(ChronoUnit.SECONDS).toString();
        assertNotNull(newMovieTime);
        ticketSelfInputDTO.setMovieTime(newMovieTime);
        String movieTimeAfter = ticketSelfInputDTO.getMovieTime();
        assertNotNull(movieTimeAfter);

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketSelfInputDTOMovieIDSetterTestPositive() {
        UUID movieIDBefore = ticketSelfInputDTO.getMovieID();
        UUID newMovieID = UUID.randomUUID();
        ticketSelfInputDTO.setMovieID(newMovieID);
        UUID movieIDAfter = ticketSelfInputDTO.getMovieID();

        assertEquals(newMovieID, movieIDAfter);
        assertNotEquals(movieIDBefore, movieIDAfter);
    }
}
