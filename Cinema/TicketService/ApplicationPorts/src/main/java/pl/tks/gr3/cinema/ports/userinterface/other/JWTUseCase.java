package pl.tks.gr3.cinema.ports.userinterface.other;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTUseCase {

    String extractUsername(String jwtToken);
    boolean isTokenValid(String jwtToken, UserDetails userDetails);
    boolean isTokenValid(String jwtToken);
}
