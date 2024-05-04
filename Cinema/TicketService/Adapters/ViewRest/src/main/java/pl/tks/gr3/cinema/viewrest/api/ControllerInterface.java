package pl.tks.gr3.cinema.viewrest.api;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ControllerInterface<Type> {

    // CRUD methods

    // Read methods

    ResponseEntity<?> findByUUID(UUID elementID);
    ResponseEntity<?> findAll();
}
