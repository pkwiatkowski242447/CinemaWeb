package pl.pas.gr3.cinema.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.model.UserFile;
import pl.pas.gr3.cinema.model.users.Account;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.AccountRepository;
import pl.pas.gr3.cinema.services.interfaces.IAccountService;
import pl.pas.gr3.cinema.services.interfaces.IFileSystemService;

@Service
@RequiredArgsConstructor
@LoggerInterceptor
public class AccountService implements IAccountService {

    private final AccountRepository accountRepository;
    private final IFileSystemService fileSystemService;

    // Create methods

    @Override
    public Account createClientAccount(String login, String password, String firstName, String lastName,
                                       String email, String phoneNumber, String language, MultipartFile avatar) {
        Account clientAccount = createAccount(login, password, firstName, lastName, email, phoneNumber, language, avatar);
        Client client = new Client();
        clientAccount.addAccessLevel(client);
        return this.accountRepository.saveAndFlush(clientAccount);
    }

    @Override
    public Account createStaffAccount(String login, String password, String firstName, String lastName,
                                      String email, String phoneNumber, String language, MultipartFile avatar) {
        Account staffAccount = createAccount(login, password, firstName, lastName, email, phoneNumber, language, avatar);
        Staff staff = new Staff();
        staffAccount.addAccessLevel(staff);
        return this.accountRepository.saveAndFlush(staffAccount);
    }

    @Override
    public Account createAdminAccount(String login, String password, String firstName, String lastName,
                                      String email, String phoneNumber, String language, MultipartFile avatar) {
        Account adminAccount = createAccount(login, password, firstName, lastName, email, phoneNumber, language, avatar);
        Admin admin = new Admin();
        adminAccount.addAccessLevel(admin);
        return this.accountRepository.saveAndFlush(adminAccount);
    }

    private Account createAccount(String login, String password, String firstName, String lastName,
                               String email, String phoneNumber, String language, MultipartFile avatar) {
        Account account = new Account(login, password, firstName, lastName, email, phoneNumber, language);

        if (avatar != null) {
            UserFile avatarFile = this.fileSystemService.performFileWrite(avatar);
            account.setUserFile(avatarFile);
        }

        return account;
    }
}
