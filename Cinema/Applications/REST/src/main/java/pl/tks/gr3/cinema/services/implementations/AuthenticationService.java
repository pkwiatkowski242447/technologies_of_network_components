package pl.tks.gr3.cinema.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.exceptions.repositories.UserRepositoryException;
import pl.tks.gr3.cinema.exceptions.repositories.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.exceptions.services.crud.authentication.login.AuthenticationServiceLoginAdminException;
import pl.tks.gr3.cinema.exceptions.services.crud.authentication.login.AuthenticationServiceLoginClientException;
import pl.tks.gr3.cinema.exceptions.services.crud.authentication.login.AuthenticationServiceLoginStaffException;
import pl.tks.gr3.cinema.exceptions.services.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.pas.gr3.cinema.exceptions.services.crud.authentication.register.*;
import pl.tks.gr3.cinema.exceptions.services.crud.authentication.register.*;
import pl.tks.gr3.cinema.model.users.Admin;
import pl.tks.gr3.cinema.model.users.Client;
import pl.tks.gr3.cinema.model.users.Staff;
import pl.tks.gr3.cinema.adapters.repositories.implementations.UserRepository;
import pl.tks.gr3.cinema.services.interfaces.AuthenticationServiceInterface;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
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
