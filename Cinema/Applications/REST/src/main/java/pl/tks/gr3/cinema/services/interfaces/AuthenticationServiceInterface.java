package pl.tks.gr3.cinema.services.interfaces;

import pl.tks.gr3.cinema.exceptions.services.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.tks.gr3.cinema.exceptions.services.crud.authentication.register.GeneralAuthenticationRegisterException;
import pl.tks.gr3.cinema.model.users.Admin;
import pl.tks.gr3.cinema.model.users.Client;
import pl.tks.gr3.cinema.model.users.Staff;

public interface AuthenticationServiceInterface {

    Client registerClient(String clientUsername, String clientPassword) throws GeneralAuthenticationRegisterException;
    Admin registerAdmin(String adminUsername, String adminPassword) throws GeneralAuthenticationRegisterException;
    Staff registerStaff(String staffUsername, String staffPassword) throws GeneralAuthenticationRegisterException;

    Client loginClient(String clientUsername, String clientPassword) throws GeneralAuthenticationLoginException;
    Admin loginAdmin(String adminUsername, String adminPassword) throws GeneralAuthenticationLoginException;
    Staff loginStaff(String staffUsername, String staffPassword) throws GeneralAuthenticationLoginException;
}
