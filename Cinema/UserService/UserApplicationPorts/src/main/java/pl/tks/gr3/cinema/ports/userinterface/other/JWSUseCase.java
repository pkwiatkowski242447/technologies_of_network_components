package pl.tks.gr3.cinema.ports.userinterface.other;

import pl.tks.gr3.cinema.domain_model.User;

public interface JWSUseCase {

    String generateSignatureForUser(User user);
    boolean verifyUserSignature(String signature, User user);
    String extractUsernameFromSignature(String signature);
}
