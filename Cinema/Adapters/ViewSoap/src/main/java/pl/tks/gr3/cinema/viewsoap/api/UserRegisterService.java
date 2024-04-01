package pl.tks.gr3.cinema.viewsoap.api;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;

@WebService
public interface UserRegisterService {

    @WebMethod
    Client registerClient(String login, String password);

    @WebMethod
    Staff registerStaff(String login, String password);

    @WebMethod
    Admin registerAdmin(String login, String password);
}
