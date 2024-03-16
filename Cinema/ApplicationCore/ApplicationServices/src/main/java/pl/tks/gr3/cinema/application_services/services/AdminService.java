package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.*;
import pl.tks.gr3.cinema.adapters.aggregates.UserRepositoryAdapter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryAdminNotFoundException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements UserServiceInterface<Admin> {

    private UserRepositoryAdapter userRepository;

    public AdminService() {
    }

    @Autowired
    public AdminService(UserRepositoryAdapter userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Admin create(String login, String password) throws AdminServiceCreateException {
        try {
            return this.userRepository.createAdmin(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new AdminServiceCreateAdminDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByUUID(UUID adminID) throws AdminServiceReadException {
        try {
            return this.userRepository.findAdminByUUID(adminID);
        } catch (UserRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByLogin(String login) throws AdminServiceReadException {
        try {
            return this.userRepository.findAdminByLogin(login);
        } catch (UserRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAllMatchingLogin(String loginToBeMatched) throws AdminServiceReadException {
        try {
            return this.userRepository.findAllAdminsMatchingLogin(loginToBeMatched);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAll() throws AdminServiceReadException {
        try {
            return this.userRepository.findAllAdmins();
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Admin admin) throws AdminServiceUpdateException {
        try {
            this.userRepository.updateAdmin(admin);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID adminID) throws AdminServiceActivationException {
        try {
            this.userRepository.activate(this.userRepository.findAdminByUUID(adminID));
        } catch (UserRepositoryException exception) {
            throw new AdminServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID adminID) throws AdminServiceDeactivationException {
        try {
            this.userRepository.deactivate(this.userRepository.findAdminByUUID(adminID));
        } catch (UserRepositoryException exception) {
            throw new AdminServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForUser(UUID adminID) throws AdminServiceReadException {
        try {
            return this.userRepository.getListOfTickets(adminID, UserEntConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws AdminServiceDeleteException {
        try {
            this.userRepository.delete(userID, UserEntConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
