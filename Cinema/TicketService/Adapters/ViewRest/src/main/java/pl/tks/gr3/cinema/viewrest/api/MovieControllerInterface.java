package pl.tks.gr3.cinema.viewrest.api;

import org.springframework.http.ResponseEntity;
import pl.tks.gr3.cinema.viewrest.model.movies.MovieInputDTO;

import java.util.UUID;

public interface MovieControllerInterface {

    // Create methods

    ResponseEntity<?> create(MovieInputDTO movieInputDTO);

    // Read methods

    ResponseEntity<?> findByUUID(UUID movieID);
    ResponseEntity<?> findAll();

    // Update methods

    ResponseEntity<?> delete(UUID movieID);

    // Other methods

    ResponseEntity<?> findAllTicketsForCertainMovie(UUID movieID);
}
