package pl.tks.gr3.cinema.services.interfaces;

import pl.tks.gr3.cinema.exceptions.services.GeneralServiceException;

import java.util.List;
import java.util.UUID;

public interface ServiceInterface<Type> {

    // Read methods

    Type findByUUID(UUID elementID) throws GeneralServiceException;
    List<Type> findAll() throws GeneralServiceException;
}
