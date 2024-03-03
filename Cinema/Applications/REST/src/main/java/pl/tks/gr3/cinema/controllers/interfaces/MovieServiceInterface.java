package pl.tks.gr3.cinema.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import pl.pas.gr3.dto.input.MovieInputDTO;
import pl.tks.gr3.cinema.model.Movie;

import java.util.UUID;

public interface MovieServiceInterface extends ServiceInterface<Movie> {

    // Create methods

    ResponseEntity<?> create(MovieInputDTO movieInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID movieID);

    // Other methods

    ResponseEntity<?> findAllTicketsForCertainMovie(UUID movieID);
}
