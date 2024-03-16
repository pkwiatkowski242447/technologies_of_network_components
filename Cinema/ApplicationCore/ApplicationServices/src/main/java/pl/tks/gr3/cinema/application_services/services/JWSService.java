package pl.tks.gr3.cinema.application_services.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.consts.model.MovieEntConstants;
import pl.tks.gr3.cinema.adapters.consts.model.TicketEntConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.ports.userinterface.JWSServiceInterface;

import java.util.Base64;

@Service
public class JWSService implements JWSServiceInterface {

    @Value("${security.jwt.token.secret-key:secret-value}")
    private String SECRET_KEY;

    @Override
    public String generateSignatureForUser(User user) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(UserEntConstants.USER_ID, user.getUserID().toString())
                .withClaim(UserEntConstants.USER_LOGIN, user.getUserLogin())
                .withClaim(UserEntConstants.USER_STATUS_ACTIVE, user.isUserStatusActive())
                .sign(algorithm);
    }

    @Override
    public String generateSignatureForMovie(Movie movie) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(MovieEntConstants.MOVIE_ID, movie.getMovieID().toString())
                .sign(algorithm);
    }

    @Override
    public String generateSignatureForTicket(Ticket ticket) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(TicketEntConstants.TICKET_ID, ticket.getTicketID().toString())
                .withClaim(TicketEntConstants.TICKET_FINAL_PRICE, ticket.getTicketPrice())
                .withClaim(TicketEntConstants.USER_ID, ticket.getUserID().toString())
                .withClaim(TicketEntConstants.MOVIE_ID, ticket.getMovieID().toString())
                .sign(algorithm);
    }

    @Override
    public String extractUsernameFromSignature(String signature) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(signature);
        return decodedJWT.getClaim(UserEntConstants.USER_LOGIN).asString();
    }

    @Override
    public boolean verifyUserSignature(String signature, User user) {
        String currentSignature = this.generateSignatureForUser(user);
        return signature.equals(currentSignature);
    }

    @Override
    public boolean verifyMovieSignature(String signature, Movie movie) {
        String currentSignature = this.generateSignatureForMovie(movie);
        return signature.equals(currentSignature);
    }

    @Override
    public boolean verifyTicketSignature(String signature, Ticket ticket) {
        String currentSignature = this.generateSignatureForTicket(ticket);
        return signature.equals(currentSignature);
    }

    private String getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
