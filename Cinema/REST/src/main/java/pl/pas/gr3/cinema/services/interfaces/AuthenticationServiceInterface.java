package pl.pas.gr3.cinema.services.interfaces;

import pl.pas.gr3.dto.auth.LoginOutputDTO;
import pl.pas.gr3.dto.auth.RegisterOutputDTO;

public interface AuthenticationServiceInterface {

    RegisterOutputDTO registerClient(String clientUsername, String clientPassword);
    RegisterOutputDTO registerAdmin(String adminUsername, String adminPassword);
    RegisterOutputDTO registerStaff(String staffUsername, String staffPassword);

    LoginOutputDTO loginClient(String clientUsername, String clientPassword);
    LoginOutputDTO loginAdmin(String adminUsername, String adminPassword);
    LoginOutputDTO loginStaff(String staffUsername, String staffPassword);
}
