package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.application_services.services.JWSService;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JWSServiceTest {

    private final JWSService jwsService = new JWSService();

    private static Client clientNo1;
    private static Movie movieNo1;
    private static Ticket ticketNo1;

    @BeforeEach
    public void initializeSampleData() {
        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        movieNo1 = new Movie(UUID.randomUUID(), "UniqueMovieNameNo1", 10, 1, 10);
        ticketNo1 = new Ticket(UUID.randomUUID(), LocalDateTime.now(), movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
    }

    @Test
    public void adminServiceAllArgsConstructorTestPositive() {
        JWSService jwsService = new JWSService();
        assertNotNull(jwsService);
    }

    @Test
    public void testGenerateSignatureForUser() {
        String signature = jwsService.generateSignatureForUser(clientNo1);
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    public void testGenerateSignatureForMovie() {
        String signature = jwsService.generateSignatureForMovie(movieNo1);
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    public void testGenerateSignatureForTicket() {
        String signature = jwsService.generateSignatureForTicket(ticketNo1);
        assertNotNull(signature);
        assertFalse(signature.isEmpty());
    }

    @Test
    public void testExtractUsernameFromSignature() {
        String signature = jwsService.generateSignatureForUser(clientNo1);
        String extractedUsername = jwsService.extractUsernameFromSignature(signature);
        assertEquals(clientNo1.getUserLogin(), extractedUsername);
    }

    @Test
    public void testVerifyUserSignatureWithValidSignature() {
        String signature = jwsService.generateSignatureForUser(clientNo1);
        assertTrue(jwsService.verifyUserSignature(signature, clientNo1));
    }

    @Test
    public void testVerifyUserSignatureWithInvalidSignature() {
        assertFalse(jwsService.verifyUserSignature("invalidSignature", clientNo1));
    }

    @Test
    public void testVerifyMovieSignatureWithValidSignature() {
        String signature = jwsService.generateSignatureForMovie(movieNo1);
        assertTrue(jwsService.verifyMovieSignature(signature, movieNo1));
    }

    @Test
    public void testVerifyMovieSignatureWithInvalidSignature() {
        assertFalse(jwsService.verifyMovieSignature("invalidSignature", movieNo1));
    }

    @Test
    public void testVerifyTicketSignatureWithValidSignature() {
        String signature = jwsService.generateSignatureForTicket(ticketNo1);
        assertTrue(jwsService.verifyTicketSignature(signature, ticketNo1));
    }

    @Test
    public void testVerifyTicketSignatureWithInvalidSignature() {
        assertFalse(jwsService.verifyTicketSignature("invalidSignature", ticketNo1));
    }
}
