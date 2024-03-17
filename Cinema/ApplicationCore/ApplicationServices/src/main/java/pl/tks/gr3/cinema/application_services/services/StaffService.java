package pl.tks.gr3.cinema.application_services.services;

import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryStaffNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.*;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;
import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;


import java.util.List;
import java.util.UUID;

public class StaffService implements UserServiceInterface<Staff> {

    private final CreateUserPort createUserPort;
    private final ReadUserPort readUserPort;
    private final UpdateUserPort updateUserPort;
    private final ActivateUserPort activateUserPort;
    private final DeactivateUserPort deactivateUserPort;
    private final DeleteUserPort deleteUserPort;

    public StaffService(CreateUserPort createUserPort,
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
    public Staff create(String login, String password) throws StaffServiceCreateException {
        try {
            return this.createUserPort.createStaff(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new StaffServiceCreateStaffDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByUUID(UUID staffID) throws StaffServiceReadException {
        try {
            return this.readUserPort.findStaffByUUID(staffID);
        } catch (UserRepositoryStaffNotFoundException exception) {
            throw new StaffServiceStaffNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByLogin(String login) throws StaffServiceReadException {
        try {
            return this.readUserPort.findStaffByLogin(login);
        } catch (UserRepositoryStaffNotFoundException exception) {
            throw new StaffServiceStaffNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAllMatchingLogin(String loginToBeMatched) throws StaffServiceReadException {
        try {
            return this.readUserPort.findAllStaffsMatchingLogin(loginToBeMatched);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAll() throws StaffServiceReadException {
        try {
            return this.readUserPort.findAllStaffs();
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Staff staff) throws StaffServiceUpdateException {
        try {
            this.updateUserPort.updateStaff(staff);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID staffID) throws StaffServiceActivationException {
        try {
            this.activateUserPort.activate(this.readUserPort.findByUUID(staffID));
        } catch (UserRepositoryException exception) {
            throw new StaffServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID staffID) throws StaffServiceDeactivationException {
        try {
            this.deactivateUserPort.deactivate(this.readUserPort.findByUUID(staffID));
        } catch (UserRepositoryException exception) {
            throw new StaffServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForUser(UUID staffID) throws StaffServiceReadException {
        try {
            return this.readUserPort.getListOfTickets(staffID, UserEntConstants.STAFF_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws StaffServiceDeleteException {
        try {
            this.deleteUserPort.delete(userID, UserEntConstants.STAFF_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
