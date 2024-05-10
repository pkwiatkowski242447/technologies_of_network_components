package pl.tks.gr3.cinema.ports.infrastructure;

import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Staff;

public interface CreateUserPort {

    Client createClient(String clientLogin, String clientPassword);
    Admin createAdmin(String adminLogin, String adminPassword);
    Staff createStaff(String staffLogin, String staffPassword);
}
