package pl.tks.gr3.cinema.security.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.consts.model.MovieConstants;
import pl.tks.gr3.cinema.adapters.consts.model.TicketConstants;
import pl.tks.gr3.cinema.adapters.consts.model.UserConstants;
import pl.tks.gr3.cinema.model.Movie;
import pl.tks.gr3.cinema.model.Ticket;
import pl.tks.gr3.cinema.model.users.User;

@Service
public class JWSService {

    @Value("${security.jwt.token.secret-key:secret-value}")
    private String SECRET_KEY;

    public String generateSignatureForUser(User user) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(UserConstants.USER_ID, user.getUserID().toString())
                .withClaim(UserConstants.USER_LOGIN, user.getUserLogin())
                .withClaim(UserConstants.USER_STATUS_ACTIVE, user.isUserStatusActive())
                .sign(algorithm);
    }

    public String generateSignatureForMovie(Movie movie) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(MovieConstants.MOVIE_ID, movie.getMovieID().toString())
                .sign(algorithm);
    }

    public String generateSignatureForTicket(Ticket ticket) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(TicketConstants.TICKET_ID, ticket.getTicketID().toString())
                .withClaim(TicketConstants.TICKET_FINAL_PRICE, ticket.getTicketPrice())
                .withClaim(TicketConstants.USER_ID, ticket.getUserID().toString())
                .withClaim(TicketConstants.MOVIE_ID, ticket.getMovieID().toString())
                .sign(algorithm);
    }

    public String extractUsernameFromSignature(String signature) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(signature);
        return decodedJWT.getClaim(UserConstants.USER_LOGIN).asString();
    }

    public boolean verifyUserSignature(String signature, User user) {
        String currentSignature = this.generateSignatureForUser(user);
        return signature.equals(currentSignature);
    }

    public boolean verifyMovieSignature(String signature, Movie movie) {
        String currentSignature = this.generateSignatureForMovie(movie);
        return signature.equals(currentSignature);
    }

    public boolean verifyTicketSignature(String signature, Ticket ticket) {
        String currentSignature = this.generateSignatureForTicket(ticket);
        return signature.equals(currentSignature);
    }

    private String getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
