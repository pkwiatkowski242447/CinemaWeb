package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;

import java.util.List;
import java.util.UUID;

public interface ClientRepositoryInterface extends RepositoryInterface<Client> {

    // Create methods

    Client createClient(String clientLogin, String clientPassword);
    Admin createAdmin(String adminLogin, String adminPassword);
    Staff createStaff(String staffLogin, String staffPassword);

    // Read methods

    Client findByLogin(String loginValue);
    List<Client> findAllMatchingLogin(String loginValue);

    // Update methods

    void activate(Client client);
    void deactivate(Client client);

    // Other required methods

    List<Ticket> getListOfTicketsForClient(UUID clientID);
}
