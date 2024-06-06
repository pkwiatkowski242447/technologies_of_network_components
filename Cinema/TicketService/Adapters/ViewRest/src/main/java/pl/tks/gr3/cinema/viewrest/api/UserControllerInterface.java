package pl.tks.gr3.cinema.viewrest.api;

import org.springframework.http.ResponseEntity;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserUpdateDTO;

import java.util.UUID;

public interface UserControllerInterface {

    // Create methods

    ResponseEntity<?> create(UserInputDTO userInputDTO);

    // Update methods

    ResponseEntity<?> update(String ifMatch, UserUpdateDTO userUpdateDto);

    // Activate

    ResponseEntity<?> activate(UUID userID);

    // Deactivate

    ResponseEntity<?> deactivate(UUID userID);
}
