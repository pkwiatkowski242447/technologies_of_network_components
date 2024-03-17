package pl.tks.gr3.cinema.ports.userinterface;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ServiceInterface<Type> {

    // Read methods

    Type findByUUID(UUID elementID);
    List<Type> findAll();
}
