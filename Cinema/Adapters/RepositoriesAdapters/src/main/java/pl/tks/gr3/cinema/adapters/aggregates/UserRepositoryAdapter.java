package pl.tks.gr3.cinema.adapters.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.api.UserRepositoryInterface;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.adapters.converters.TicketConverter;
import pl.tks.gr3.cinema.adapters.converters.UserConverter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.other.client.UserTypeNotFoundException;
import pl.tks.gr3.cinema.adapters.messages.MongoRepositoryMessages;
import pl.tks.gr3.cinema.adapters.model.users.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.users.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.users.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.users.UserEnt;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.List;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements CreateUserPort, ReadUserPort, UpdateUserPort, ActivateUserPort, DeactivateUserPort, DeleteUserPort {

    private final UserRepositoryInterface userRepository;

    @Autowired
    public UserRepositoryAdapter(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    // C

    @Override
    public Client createClient(String clientLogin, String clientPassword) throws UserRepositoryException {
        return UserConverter.convertToClient(userRepository.createClient(clientLogin, clientPassword));
    }

    @Override
    public Admin createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException {
        return UserConverter.convertToAdmin(userRepository.createAdmin(adminLogin, adminPassword));
    }

    @Override
    public Staff createStaff(String staffLogin, String staffPassword) throws UserRepositoryException {
        return UserConverter.convertToStaff(userRepository.createStaff(staffLogin, staffPassword));
    }

    // R

    // TODO: try to do it using switch instead

    @Override
    public User findByUUID(UUID userID) throws UserRepositoryException {
        UserEnt userEnt = this.userRepository.findByUUID(userID);
        User user;
        if (userEnt.getClass().equals(ClientEnt.class)) {
            user = UserConverter.convertToClient(userEnt);
        } else if (userEnt.getClass().equals(StaffEnt.class)) {
            user = UserConverter.convertToStaff(userEnt);
        } else if (userEnt.getClass().equals(AdminEnt.class)) {
            user = UserConverter.convertToAdmin(userEnt);
        } else {
            throw new UserTypeNotFoundException(MongoRepositoryMessages.USER_TYPE_NOT_FOUND);
        }
        return user;
    }

    @Override
    public Client findClientByUUID(UUID clientID) throws UserRepositoryException {
        return UserConverter.convertToClient(userRepository.findClientByUUID(clientID));
    }

    @Override
    public Admin findAdminByUUID(UUID adminID) throws UserRepositoryException {
        return UserConverter.convertToAdmin(userRepository.findAdminByUUID(adminID));
    }

    @Override
    public Staff findStaffByUUID(UUID staffID) throws UserRepositoryException {
        return UserConverter.convertToStaff(userRepository.findStaffByUUID(staffID));
    }

    @Override
    public Client findClientByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertToClient(userRepository.findClientByLogin(loginValue));
    }

    @Override
    public Admin findAdminByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertToAdmin(userRepository.findAdminByLogin(loginValue));
    }

    @Override
    public Staff findStaffByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertToStaff(userRepository.findStaffByLogin(loginValue));
    }

    @Override
    public List<Client> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException {
        return userRepository.findAllClientsMatchingLogin(loginValue).stream().map(UserConverter::convertToClient).toList();
    }

    @Override
    public List<Admin> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException {
        return userRepository.findAllAdminsMatchingLogin(loginValue).stream().map(UserConverter::convertToAdmin).toList();
    }

    @Override
    public List<Staff> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException {
        return userRepository.findAllStaffsMatchingLogin(loginValue).stream().map(UserConverter::convertToStaff).toList();
    }

    @Override
    public List<Client> findAllClients() throws UserRepositoryException {
        return userRepository.findAllClients().stream().map(UserConverter::convertToClient).toList();
    }

    @Override
    public List<Admin> findAllAdmins() throws UserRepositoryException {
        return userRepository.findAllAdmins().stream().map(UserConverter::convertToAdmin).toList();
    }

    @Override
    public List<Staff> findAllStaffs() throws UserRepositoryException {
        return userRepository.findAllStaffs().stream().map(UserConverter::convertToStaff).toList();
    }

    @Override
    public List<Ticket> getListOfTickets(UUID userID, String discriminator) throws UserRepositoryException {
        return this.userRepository.getListOfTicketsForClient(userID, discriminator).stream().map(TicketConverter::convertToTicket).toList();
    }

    // U

    @Override
    public void updateClient(Client client) throws UserRepositoryException {
        userRepository.updateClient(UserConverter.convertToClientEnt(client));
    }

    @Override
    public void updateAdmin(Admin admin) throws UserRepositoryException {
        userRepository.updateAdmin(UserConverter.convertToAdminEnt(admin));
    }

    @Override
    public void updateStaff(Staff staff) throws UserRepositoryException {
        userRepository.updateStaff(UserConverter.convertToStaffEnt(staff));
    }

    // D

    @Override
    public void delete(UUID userID, String name) throws UserRepositoryException {
        userRepository.delete(userID, name);
    }

    // Other

    // TODO: try to do it using switch

    @Override
    public void activate(User user) throws UserRepositoryException {
        if (user.getClass().equals(Client.class)) {
            userRepository.activate(UserConverter.convertToClientEnt(user), UserEntConstants.CLIENT_DISCRIMINATOR);
        } else if (user.getClass().equals(Admin.class)) {
            userRepository.activate(UserConverter.convertToAdminEnt(user), UserEntConstants.ADMIN_DISCRIMINATOR);
        } else if (user.getClass().equals(Staff.class)) {
            userRepository.activate(UserConverter.convertToStaffEnt(user), UserEntConstants.STAFF_DISCRIMINATOR);
        } else {
            throw new UserTypeNotFoundException(MongoRepositoryMessages.USER_TYPE_NOT_FOUND);
        }
    }

    // TODO: try doing it with switch

    @Override
    public void deactivate(User user) throws UserRepositoryException {
        if (user.getClass().equals(Client.class)) {
            userRepository.deactivate(UserConverter.convertToClientEnt(user), UserEntConstants.CLIENT_DISCRIMINATOR);
        } else if (user.getClass().equals(Admin.class)) {
            userRepository.deactivate(UserConverter.convertToAdminEnt(user), UserEntConstants.ADMIN_DISCRIMINATOR);
        } else if (user.getClass().equals(Staff.class)) {
            userRepository.deactivate(UserConverter.convertToStaffEnt(user), UserEntConstants.STAFF_DISCRIMINATOR);
        } else {
            throw new UserTypeNotFoundException(MongoRepositoryMessages.USER_TYPE_NOT_FOUND);
        }
    }
}
