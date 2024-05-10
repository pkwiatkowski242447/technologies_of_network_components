package pl.tks.gr3.cinema.ports.infrastructure;

import java.util.UUID;

public interface DeleteUserPort {

    void delete(UUID userID, String name);
}
