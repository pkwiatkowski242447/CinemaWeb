package pl.pas.gr3.cinema.services.implementations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.exceptions.ApplicationInputOutputException;
import pl.pas.gr3.cinema.model.UserFile;
import pl.pas.gr3.cinema.model.users.Account;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.AccountRepository;
import pl.pas.gr3.cinema.services.interfaces.IAccountService;
import pl.pas.gr3.cinema.utils.I18n;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@LoggerInterceptor
public class AccountService implements IAccountService {

    @Value("${avatar.files.directory}")
    private String avatarsDirectory;

    private final AccountRepository accountRepository;

    // Lifecycle methods

    @PostConstruct
    private void initializeServerState() {
        File avatarsDir = new File(avatarsDirectory);
        if (!avatarsDir.exists() && !avatarsDir.mkdirs()) {
            throw new ApplicationInputOutputException(
                    I18n.APPLICATION_COMPONENT_INITIALIZATION_EXCEPTION);
        }
    }

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
            String fileName = this.performFileWrite(avatar);
            UserFile avatarFile = new UserFile(fileName, avatar.getOriginalFilename(), avatar.getContentType());
            account.setUserFile(avatarFile);
        }

        return account;
    }

    private String performFileWrite(MultipartFile avatar) {
        String fileName = this.avatarsDirectory + Instant.now();
        File createdFile = new File(fileName);

        try {
            if (createdFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    fos.write(avatar.getBytes());
                }
            }
        } catch (IOException exception) {
            throw new ApplicationInputOutputException();
        }

        return fileName;
    }
}
