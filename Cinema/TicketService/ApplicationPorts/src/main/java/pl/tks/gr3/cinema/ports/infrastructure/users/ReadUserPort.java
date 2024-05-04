package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.domain_model.users.User;

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

    List<Ticket> getListOfTickets(UUID userID, String discriminator);
}
