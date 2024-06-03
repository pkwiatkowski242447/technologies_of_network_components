package pl.tks.gr3.cinema.ports.infrastructure.users;

import java.util.UUID;

public interface DeleteUserPort {
    void delete(UUID userID);
}
