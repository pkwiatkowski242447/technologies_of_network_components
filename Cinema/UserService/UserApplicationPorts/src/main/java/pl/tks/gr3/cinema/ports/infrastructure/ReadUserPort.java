package pl.tks.gr3.cinema.ports.infrastructure;

import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.domain_model.User;

import java.util.List;
import java.util.UUID;

public interface ReadUserPort {

    User findByUUID(UUID userID);

    Client findClientByUUID(UUID clientID);
    Admin findAdminByUUID(UUID adminID);
    Staff findStaffByUUID(UUID staffID);

    Client findClientByLogin(String loginValue);
    Admin findAdminByLogin(String loginValue);
    Staff findStaffByLogin(String loginValue);

    List<Client> findAllClientsMatchingLogin(String loginValue);
    List<Admin> findAllAdminsMatchingLogin(String loginValue);
    List<Staff> findAllStaffsMatchingLogin(String loginValue);

    List<Client> findAllClients();
    List<Admin> findAllAdmins();
    List<Staff> findAllStaffs();
}
