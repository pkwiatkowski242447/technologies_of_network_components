package pl.tks.gr3.cinema.ports.userinterface;

import java.util.List;
import java.util.UUID;

public interface ServiceInterface<Type> {

    // Read methods

    Type findByUUID(UUID elementID);
    List<Type> findAll();
}
