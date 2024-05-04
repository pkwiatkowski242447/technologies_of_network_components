package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Staff;

public interface CreateUserPort {

    Client createClient(String clientLogin, String clientPassword);
    Admin createAdmin(String adminLogin, String adminPassword);
    Staff createStaff(String staffLogin, String staffPassword);
}
