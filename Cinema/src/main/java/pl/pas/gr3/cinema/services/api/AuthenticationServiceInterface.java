package pl.pas.gr3.cinema.services.api;

import pl.pas.gr3.cinema.exception.services.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.pas.gr3.cinema.exception.services.crud.authentication.register.GeneralAuthenticationRegisterException;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;

public interface AuthenticationServiceInterface {

    Client registerClient(String clientUsername, String clientPassword) throws GeneralAuthenticationRegisterException;
    Admin registerAdmin(String adminUsername, String adminPassword) throws GeneralAuthenticationRegisterException;
    Staff registerStaff(String staffUsername, String staffPassword) throws GeneralAuthenticationRegisterException;

    Client loginClient(String clientUsername, String clientPassword) throws GeneralAuthenticationLoginException;
    Admin loginAdmin(String adminUsername, String adminPassword) throws GeneralAuthenticationLoginException;
    Staff loginStaff(String staffUsername, String staffPassword) throws GeneralAuthenticationLoginException;
}
