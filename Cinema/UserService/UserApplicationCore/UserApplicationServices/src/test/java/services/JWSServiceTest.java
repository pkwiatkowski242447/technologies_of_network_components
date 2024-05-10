package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tks.gr3.cinema.application_services.services.JWSService;
import pl.tks.gr3.cinema.domain_model.Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JWSServiceTest {

    private final JWSService jwsService = new JWSService();

    private static Client clientNo1;

    @BeforeEach
    public void initializeSampleData() {
        clientNo1 = new Client(UUID.randomUUID(), "UniqueClientLoginNo1", "UniqueClientPasswordNo1");
    }

    @Test
    public void jwsServiceAllArgsConstructorTestPositive() {
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
}
