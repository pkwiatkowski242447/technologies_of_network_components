package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.application_services.services.JWTService;
import pl.tks.gr3.cinema.domain_model.users.Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JWTServiceTest {

    private final JWTService jwtService = new JWTService();

    private static Client clientNo1;
    private static Client clientNo2;

    @BeforeEach
    public void initializeSampleData() {
        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
        clientNo2 = new Client(UUID.randomUUID(), "UniqueClientLoginNo2", "UniqueClientPasswordNo2");
    }

    @Test
    public void jwtServiceAllArgsConstructorTestPositive() {
        JWTService jwtService = new JWTService();
        assertNotNull(jwtService);
    }

    @Test
    public void testGenerateJWTTokenForUser() {
        String jwt = jwtService.generateJWTToken(clientNo1);
        assertNotNull(jwt);
        assertFalse(jwt.isEmpty());
    }

    @Test
    public void testExtractUsernameFromJWTTestPositive() {
        String jwt = jwtService.generateJWTToken(clientNo1);
        String extractedUsername = jwtService.extractUsername(jwt);
        assertEquals(clientNo1.getUserLogin(), extractedUsername);
    }

    @Test
    public void testExtractUsernameFromJWTTestNegative() {
        String jwt = jwtService.generateJWTToken(clientNo1);
        String extractedUsernameNo1 = jwtService.extractUsername(jwt);
        assertNotEquals(clientNo2.getUserLogin(), extractedUsernameNo1);
    }
}
