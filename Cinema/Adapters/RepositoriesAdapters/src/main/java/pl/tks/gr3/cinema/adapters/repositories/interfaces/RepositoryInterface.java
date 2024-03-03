package pl.tks.gr3.cinema.adapters.repositories.interfaces;

import pl.tks.gr3.cinema.adapters.exceptions.GeneralRepositoryException;

import java.util.UUID;

public interface RepositoryInterface<Type> {

    // CRUD methods

    // Read methods

    Type findByUUID(UUID elementID) throws GeneralRepositoryException;
}
