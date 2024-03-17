package pl.tks.gr3.cinema.application_services.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginAdminException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginClientException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginStaffException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.*;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.AuthenticationServiceInterface;

public class AuthenticationService implements AuthenticationServiceInterface {

    private final UserRepositoryAdapter userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepositoryAdapter userRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Client registerClient(String clientUsername, String clientPassword) throws GeneralAuthenticationRegisterException {
        try {
            return userRepository.createClient(clientUsername, passwordEncoder.encode(clientPassword));
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new AuthenticationServiceUserWithGivenLoginExistsException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AuthenticationServiceRegisterClientException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin registerAdmin(String adminUsername, String adminPassword) throws GeneralAuthenticationRegisterException {
        try {
            return userRepository.createAdmin(adminUsername, passwordEncoder.encode(adminPassword));
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new AuthenticationServiceUserWithGivenLoginExistsException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AuthenticationServiceRegisterAdminException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff registerStaff(String staffUsername, String staffPassword) throws GeneralAuthenticationRegisterException {
        try {
            return userRepository.createStaff(staffUsername, passwordEncoder.encode(staffPassword));
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new AuthenticationServiceUserWithGivenLoginExistsException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AuthenticationServiceRegisterStaffException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client loginClient(String clientUsername, String clientPassword) throws GeneralAuthenticationLoginException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(clientUsername, clientPassword));
        try {
            return userRepository.findClientByLogin(clientUsername);
        } catch (UserRepositoryException exception) {
            throw new AuthenticationServiceLoginClientException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin loginAdmin(String adminUsername, String adminPassword) throws GeneralAuthenticationLoginException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(adminUsername, adminPassword));
        try {
            return userRepository.findAdminByLogin(adminUsername);
        } catch (UserRepositoryException exception) {
            throw new AuthenticationServiceLoginAdminException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff loginStaff(String staffUsername, String staffPassword) throws GeneralAuthenticationLoginException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(staffUsername, staffPassword));
        try {
            return userRepository.findStaffByLogin(staffUsername);
        } catch (UserRepositoryException exception) {
            throw new AuthenticationServiceLoginStaffException(exception.getMessage(), exception);
        }
    }
}
