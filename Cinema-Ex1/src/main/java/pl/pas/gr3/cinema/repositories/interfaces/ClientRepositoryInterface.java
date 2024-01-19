package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.exceptions.repositories.GeneralRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;

import java.util.List;
import java.util.UUID;

public interface ClientRepositoryInterface extends RepositoryInterface<Client> {

    // Create methods

    Client createClient(String clientLogin, String clientPassword) throws ClientRepositoryException;
    Admin createAdmin(String adminLogin, String adminPassword) throws ClientRepositoryException;
    Staff createStaff(String staffLogin, String staffPassword) throws ClientRepositoryException;

    // Read methods

    Client findClientByLogin(String loginValue) throws ClientRepositoryException;
    Admin findAdminByLogin(String loginValue) throws ClientRepositoryException;
    Staff findStaffByLogin(String loginValue) throws ClientRepositoryException;

    List<Client> findAllClientsMatchingLogin(String loginValue) throws ClientRepositoryException;
    List<Admin> findAllAdminsMatchingLogin(String loginValue) throws ClientRepositoryException;
    List<Staff> findAllStaffsMatchingLogin(String loginValue) throws ClientRepositoryException;

    List<Client> findAllClients() throws ClientRepositoryException;
    List<Admin> findAllAdmins() throws ClientRepositoryException;
    List<Staff> findAllStaffs() throws ClientRepositoryException;

    // Update methods

    void updateClient(Client client) throws ClientRepositoryException;
    void updateAdmin(Admin admin) throws ClientRepositoryException;
    void updateStaff(Staff staff) throws ClientRepositoryException;

    void activate(Client client, String name) throws ClientRepositoryException;
    void deactivate(Client client, String name) throws ClientRepositoryException;

    // Delete methods

    void delete(UUID elementID, String name) throws GeneralRepositoryException;

    // Other required methods

    List<Ticket> getListOfTicketsForClient(UUID clientID, String name) throws ClientRepositoryException;
}
