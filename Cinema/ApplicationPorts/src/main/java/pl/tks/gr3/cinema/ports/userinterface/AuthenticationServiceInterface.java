package pl.tks.gr3.cinema.ports.userinterface;

import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;

@Service
public interface AuthenticationServiceInterface {

    Client registerClient(String clientUsername, String clientPassword);
    Admin registerAdmin(String adminUsername, String adminPassword);
    Staff registerStaff(String staffUsername, String staffPassword);

    Client loginClient(String clientUsername, String clientPassword);
    Admin loginAdmin(String adminUsername, String adminPassword);
    Staff loginStaff(String staffUsername, String staffPassword);
}
