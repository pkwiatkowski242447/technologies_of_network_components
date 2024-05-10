package pl.tks.gr3.cinema.viewrest.api;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserControllerInterface<Type> extends ControllerInterface<Type> {

    // Read methods

    ResponseEntity<?> findByLogin(String login);

    ResponseEntity<?> findAllWithMatchingLogin(String loginToMatch);

    // Activate

    ResponseEntity<?> activate(UUID userID);

    // Deactivate

    ResponseEntity<?> deactivate(UUID userID);
}
