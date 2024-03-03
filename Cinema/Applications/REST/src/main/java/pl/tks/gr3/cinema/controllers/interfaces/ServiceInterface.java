package pl.tks.gr3.cinema.controllers.interfaces;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ServiceInterface<Type> {

    // CRUD methods

    // Read methods

    ResponseEntity<?> findByUUID(UUID elementID);
    ResponseEntity<?> findAll();
}
