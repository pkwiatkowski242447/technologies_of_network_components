package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;

import java.util.UUID;

public interface DeleteUserPort {
    void delete(UUID userID, String name) throws UserRepositoryException;
}
