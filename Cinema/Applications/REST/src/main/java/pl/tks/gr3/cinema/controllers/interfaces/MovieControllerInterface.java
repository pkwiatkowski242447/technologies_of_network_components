package pl.tks.gr3.cinema.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import pl.tks.gr3.cinema.domain_model.model.Movie;
import pl.tks.gr3.cinema.dto.input.MovieInputDTO;

import java.util.UUID;

public interface MovieControllerInterface extends ControllerInterface<Movie> {

    // Create methods

    ResponseEntity<?> create(MovieInputDTO movieInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID movieID);

    // Other methods

    ResponseEntity<?> findAllTicketsForCertainMovie(UUID movieID);
}
