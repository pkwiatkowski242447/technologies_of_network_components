package pl.tks.gr3.cinema.ports.userinterface.other;

import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;

public interface RegisterUserUseCase {

    Client registerClient(String clientUsername, String clientPassword);
    Admin registerAdmin(String adminUsername, String adminPassword);
    Staff registerStaff(String staffUsername, String staffPassword);
}
