package pl.tks.gr3.cinema.adapters.aggregates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.tks.gr3.cinema.adapters.converters.UserConverter;
import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.repositories.implementations.UserRepository;
import pl.tks.gr3.cinema.domain_model.model.users.Admin;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.Staff;
import pl.tks.gr3.cinema.domain_model.model.users.User;
import pl.tks.gr3.cinema.ports.infrastructure.users.*;

import java.util.List;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements CreateUserPort, ReadUserPort, UpdateUserPort, ActivateUserPort, DeactivateUserPort, DeleteUserPort {

    private UserRepository userRepository;

    @Autowired
    public UserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // C

    @Override
    public Client createClient(String clientLogin, String clientPassword) throws UserRepositoryException {
        return UserConverter.convertClientEntToClient(userRepository.createClient(clientLogin, clientPassword));
    }

    @Override
    public Admin createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException {
        return UserConverter.convertAdminEntToAdmin(userRepository.createAdmin(adminLogin, adminPassword));
    }

    @Override
    public Staff createStaff(String staffLogin, String staffPassword) throws UserRepositoryException {
        return UserConverter.convertStaffEntToStaff(userRepository.createStaff(staffLogin, staffPassword));
    }

    // R

    @Override
    public Client findClientByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertClientEntToClient(userRepository.findClientByLogin(loginValue));
    }

    @Override
    public Admin findAdminByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertAdminEntToAdmin(userRepository.findAdminByLogin(loginValue));
    }

    @Override
    public Staff findStaffByLogin(String loginValue) throws UserRepositoryException {
        return UserConverter.convertStaffEntToStaff(userRepository.findStaffByLogin(loginValue));
    }

    @Override
    public List<Client> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException {
        return userRepository.findAllClientsMatchingLogin(loginValue).stream().map(UserConverter::convertClientEntToClient).toList();
    }

    @Override
    public List<Admin> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException {
        return userRepository.findAllAdminsMatchingLogin(loginValue).stream().map(UserConverter::convertAdminEntToAdmin).toList();
    }

    @Override
    public List<Staff> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException {
        return userRepository.findAllStaffsMatchingLogin(loginValue).stream().map(UserConverter::convertStaffEntToStaff).toList();
    }

    @Override
    public List<Client> findAllClients() throws UserRepositoryException {
        return userRepository.findAllClients().stream().map(UserConverter::convertClientEntToClient).toList();
    }

    @Override
    public List<Admin> findAllAdmins() throws UserRepositoryException {
        return userRepository.findAllAdmins().stream().map(UserConverter::convertAdminEntToAdmin).toList();
    }

    @Override
    public List<Staff> findAllStaffs() throws UserRepositoryException {
        return userRepository.findAllStaffs().stream().map(UserConverter::convertStaffEntToStaff).toList();
    }

    // U

    @Override
    public void updateClient(Client client) throws UserRepositoryException {
        userRepository.updateClient(UserConverter.convertClientToClientEnt(client));
    }

    @Override
    public void updateAdmin(Admin admin) throws UserRepositoryException {
        userRepository.updateAdmin(UserConverter.convertAdminToAdminEnt(admin));
    }

    @Override
    public void updateStaff(Staff staff) throws UserRepositoryException {
        userRepository.updateStaff(UserConverter.convertStaffToStaffEnt(staff));
    }

    // D

    @Override
    public void delete(UUID userID, String name) throws UserRepositoryException {
        userRepository.delete(userID, name);
    }

    // Other

    @Override
    public void activate(User user, String name) throws UserRepositoryException {
        userRepository.activate();
    }

    @Override
    public void deactivate(User user, String name) throws UserRepositoryException {

    }
}
