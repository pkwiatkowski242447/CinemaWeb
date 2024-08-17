package pl.pas.gr3.cinema.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import pl.pas.gr3.cinema.model.users.Account;

public interface IAccountService {

    // Create methods

    Account createClientAccount(String login, String password, String firstName, String lastName,
                                String email, String phoneNumber, String language, MultipartFile avatar);
    Account createStaffAccount(String login, String password, String firstName, String lastName,
                               String email, String phoneNumber, String language, MultipartFile avatar);
    Account createAdminAccount(String login, String password, String firstName, String lastName,
                               String email, String phoneNumber, String language, MultipartFile avatar);
}
