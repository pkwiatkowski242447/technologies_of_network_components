package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.users.Admin;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.Staff;

public interface UpdateUserPort {
    void updateClient(Client client) throws UserRepositoryException;
    void updateAdmin(Admin admin) throws UserRepositoryException;
    void updateStaff(Staff staff) throws UserRepositoryException;
}
