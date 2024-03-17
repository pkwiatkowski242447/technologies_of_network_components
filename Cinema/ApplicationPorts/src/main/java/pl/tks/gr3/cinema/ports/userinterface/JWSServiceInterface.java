package pl.tks.gr3.cinema.ports.userinterface;

import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.domain_model.Movie;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.User;

@Service
public interface JWSServiceInterface {

    String generateSignatureForUser(User user);
    String generateSignatureForMovie(Movie movie);
    String generateSignatureForTicket(Ticket ticket);

    String extractUsernameFromSignature(String signature);

    boolean verifyUserSignature(String signature, User user);
    boolean verifyMovieSignature(String signature, Movie movie);
    boolean verifyTicketSignature(String signature, Ticket ticket);
}
