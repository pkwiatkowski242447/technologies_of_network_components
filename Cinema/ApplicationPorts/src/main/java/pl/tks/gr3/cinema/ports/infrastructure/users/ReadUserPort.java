package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.Admin;
import pl.tks.gr3.cinema.domain_model.model.users.Staff;

import java.util.List;

public interface ReadUserPort {
    Client findClientByLogin(String loginValue) throws UserRepositoryException;
    Admin findAdminByLogin(String loginValue) throws UserRepositoryException;
    Staff findStaffByLogin(String loginValue) throws UserRepositoryException;

    List<Client> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException;
    List<Admin> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException;
    List<Staff> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException;


    List<Client> findAllClients() throws UserRepositoryException;
    List<Admin> findAllAdmins() throws UserRepositoryException;
    List<Staff> findAllStaffs() throws UserRepositoryException;
}
