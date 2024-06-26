package pl.tks.gr3.cinema.adapters.api;

import pl.tks.gr3.cinema.adapters.exceptions.UserRepositoryException;
import pl.tks.gr3.cinema.adapters.exceptions.GeneralRepositoryException;
import pl.tks.gr3.cinema.adapters.model.AdminEnt;
import pl.tks.gr3.cinema.adapters.model.ClientEnt;
import pl.tks.gr3.cinema.adapters.model.StaffEnt;
import pl.tks.gr3.cinema.adapters.model.UserEnt;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryInterface extends AutoCloseable {

    // Create methods

    ClientEnt createClient(String clientLogin, String clientPassword) throws UserRepositoryException;
    AdminEnt createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException;
    StaffEnt createStaff(String staffLogin, String staffPassword) throws UserRepositoryException;

    // Read methods

    UserEnt findByUUID(UUID userID) throws UserRepositoryException;

    ClientEnt findClientByUUID(UUID clientID) throws UserRepositoryException;
    AdminEnt findAdminByUUID(UUID adminID) throws UserRepositoryException;
    StaffEnt findStaffByUUID(UUID staffID) throws UserRepositoryException;

    ClientEnt findClientByLogin(String loginValue) throws UserRepositoryException;
    AdminEnt findAdminByLogin(String loginValue) throws UserRepositoryException;
    StaffEnt findStaffByLogin(String loginValue) throws UserRepositoryException;

    List<ClientEnt> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException;
    List<AdminEnt> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException;
    List<StaffEnt> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException;

    List<ClientEnt> findAllClients() throws UserRepositoryException;
    List<AdminEnt> findAllAdmins() throws UserRepositoryException;
    List<StaffEnt> findAllStaffs() throws UserRepositoryException;

    // Update methods

    void updateClient(ClientEnt client) throws UserRepositoryException;
    void updateAdmin(AdminEnt admin) throws UserRepositoryException;
    void updateStaff(StaffEnt staff) throws UserRepositoryException;

    void activate(UserEnt user, String name) throws UserRepositoryException;
    void deactivate(UserEnt user, String name) throws UserRepositoryException;

    // Delete methods

    void delete(UUID userID, String name) throws GeneralRepositoryException;

    @Override
    void close();
}
