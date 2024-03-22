package pl.tks.gr3.cinema.ports.userinterface.other;

import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;

public interface RegisterUserUseCase {

    Client registerClient(String clientUsername, String clientPassword);
    Admin registerAdmin(String adminUsername, String adminPassword);
    Staff registerStaff(String staffUsername, String staffPassword);
}
