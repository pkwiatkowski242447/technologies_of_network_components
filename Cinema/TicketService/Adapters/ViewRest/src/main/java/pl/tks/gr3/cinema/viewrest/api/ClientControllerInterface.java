package pl.tks.gr3.cinema.viewrest.api;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ClientControllerInterface extends UserControllerInterface {

    // Read methods

    ResponseEntity<?> getTicketsForCertainUser(UUID clientID);
    ResponseEntity<?> getTicketsForCertainUser();
}
