package pl.tks.gr3.cinema.application_services.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.*;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryAdminNotFoundException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;
import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements UserServiceInterface<Admin> {

    private final CreateUserPort createUserPort;
    private final ReadUserPort readUserPort;
    private final UpdateUserPort updateUserPort;
    private final ActivateUserPort activateUserPort;
    private final DeactivateUserPort deactivateUserPort;
    private final DeleteUserPort deleteUserPort;

    @Autowired
    public AdminService(CreateUserPort createUserPort,
                        ReadUserPort readUserPort,
                        UpdateUserPort updateUserPort,
                        ActivateUserPort activateUserPort,
                        DeactivateUserPort deactivateUserPort,
                        DeleteUserPort deleteUserPort) {
        this.createUserPort = createUserPort;
        this.readUserPort = readUserPort;
        this.updateUserPort = updateUserPort;
        this.activateUserPort = activateUserPort;
        this.deactivateUserPort = deactivateUserPort;
        this.deleteUserPort = deleteUserPort;
    }

    @Override
    public Admin create(String login, String password) throws AdminServiceCreateException {
        try {
            return this.createUserPort.createAdmin(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new AdminServiceCreateAdminDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByUUID(UUID adminID) throws AdminServiceReadException {
        try {
            return this.readUserPort.findAdminByUUID(adminID);
        } catch (UserRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByLogin(String login) throws AdminServiceReadException {
        try {
            return this.readUserPort.findAdminByLogin(login);
        } catch (UserRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAllMatchingLogin(String loginToBeMatched) throws AdminServiceReadException {
        try {
            return this.readUserPort.findAllAdminsMatchingLogin(loginToBeMatched);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAll() throws AdminServiceReadException {
        try {
            return this.readUserPort.findAllAdmins();
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Admin admin) throws AdminServiceUpdateException {
        try {
            this.updateUserPort.updateAdmin(admin);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID adminID) throws AdminServiceActivationException {
        try {
            this.activateUserPort.activate(this.readUserPort.findAdminByUUID(adminID));
        } catch (UserRepositoryException exception) {
            throw new AdminServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID adminID) throws AdminServiceDeactivationException {
        try {
            this.deactivateUserPort.deactivate(this.readUserPort.findAdminByUUID(adminID));
        } catch (UserRepositoryException exception) {
            throw new AdminServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForUser(UUID adminID) throws AdminServiceReadException {
        try {
            return this.readUserPort.getListOfTickets(adminID, UserEntConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws AdminServiceDeleteException {
        try {
            this.deleteUserPort.delete(userID, UserEntConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
