package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.Admin;
import pl.tks.gr3.cinema.domain_model.model.users.Staff;

public interface CreateUserPort {
    Client createClient(String clientLogin, String clientPassword) throws UserRepositoryException;
    Admin createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException;
    Staff createStaff(String staffLogin, String staffPassword) throws UserRepositoryException;
}
