package pl.pas.gr3.cinema.repository.api;

import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.entity.account.Account;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends Repository<Account> {

    /* CREATE */

    Client createClient(String login, String password);
    Admin createAdmin(String login, String password);
    Staff createStaff(String login, String password);

    /* READ */

    Client findClientByLogin(String login);
    Admin findAdminByLogin(String login);
    Staff findStaffByLogin(String login);

    List<Client> findAllClientsMatchingLogin(String login);
    List<Admin> findAllAdminsMatchingLogin(String login);
    List<Staff> findAllStaffsMatchingLogin(String login);

    List<Client> findAllClients();
    List<Admin> findAllAdmins();
    List<Staff> findAllStaffs();

    /* UPDATE */

    void updateClient(Client client);
    void updateAdmin(Admin admin);
    void updateStaff(Staff staff);

    void activate(Account account, String name);
    void deactivate(Account account, String name);

    /* DELETE */

    void delete(UUID userId, String name);

    /* OTHER */

    List<Ticket> getListOfTicketsForClient(UUID userId, String name);
}
