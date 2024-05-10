package pl.tks.gr3.cinema.ports.userinterface.other;

import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;

public interface LoginUserUseCase {

    Client loginClient(String clientUsername, String clientPassword);
    Admin loginAdmin(String adminUsername, String adminPassword);
    Staff loginStaff(String staffUsername, String staffPassword);
}
