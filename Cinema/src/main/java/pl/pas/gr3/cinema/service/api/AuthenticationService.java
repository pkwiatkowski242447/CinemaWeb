package pl.pas.gr3.cinema.service.api;

import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;

public interface AuthenticationService {

    Client registerClient(String login, String password);
    Admin registerAdmin(String login, String password);
    Staff registerStaff(String login, String password);

    Client loginClient(String login, String password);
    Admin loginAdmin(String login, String password);
    Staff loginStaff(String login, String password);
}
