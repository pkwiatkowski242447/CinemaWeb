package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.exceptions.repositories.UserRepositoryException;
import pl.pas.gr3.cinema.exceptions.repositories.GeneralRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.model.users.User;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryInterface extends RepositoryInterface<User> {

    // Create methods

    Client createClient(String clientLogin, String clientPassword) throws UserRepositoryException;
    Admin createAdmin(String adminLogin, String adminPassword) throws UserRepositoryException;
    Staff createStaff(String staffLogin, String staffPassword) throws UserRepositoryException;

    // Read methods

    Client findClientByLogin(String loginValue) throws UserRepositoryException;
    Admin findAdminByLogin(String loginValue) throws UserRepositoryException;
    Staff findStaffByLogin(String loginValue) throws UserRepositoryException;

    List<Client> findAllClientsMatchingLogin(String loginValue) throws UserRepositoryException;
    List<Admin> findAllAdminsMatchingLogin(String loginValue) throws UserRepositoryException;
    List<Staff> findAllStaffsMatchingLogin(String loginValue) throws UserRepositoryException;

    List<Client> findAllClients() throws UserRepositoryException;
    List<Admin> findAllAdmins() throws UserRepositoryException;
    List<Staff> findAllStaffs() throws UserRepositoryException;

    // Update methods

    void updateClient(Client client) throws UserRepositoryException;
    void updateAdmin(Admin admin) throws UserRepositoryException;
    void updateStaff(Staff staff) throws UserRepositoryException;

    void activate(User user, String name) throws UserRepositoryException;
    void deactivate(User user, String name) throws UserRepositoryException;

    // Delete methods

    void delete(UUID userID, String name) throws GeneralRepositoryException;

    // Other required methods

    List<Ticket> getListOfTicketsForClient(UUID userID, String name) throws UserRepositoryException;
}
