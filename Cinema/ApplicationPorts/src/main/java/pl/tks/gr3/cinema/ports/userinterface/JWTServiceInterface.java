package pl.tks.gr3.cinema.ports.userinterface;

import org.springframework.security.core.userdetails.UserDetails;
import pl.tks.gr3.cinema.domain_model.users.User;

public interface JWTServiceInterface {

    String extractUsername(String jwtToken);
    String generateJWTToken(User user);
    boolean isTokenValid(String jwtToken, UserDetails userDetails);
}
