package pl.pas.gr3.cinema.controllers.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.common.dto.input.AccountRegisterDTO;
import pl.pas.gr3.cinema.controllers.interfaces.IRegistrationController;
import pl.pas.gr3.cinema.model.users.Account;
import pl.pas.gr3.cinema.services.implementations.AccountService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@LoggerInterceptor
public class RegistrationController implements IRegistrationController {

    @Value("${account.created.url}")
    private String accountCreatedUrl;

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> registerClient(AccountRegisterDTO registerDTO) {
        Account clientAccount = this.accountService.createClientAccount(registerDTO.login(),
                passwordEncoder.encode(registerDTO.password()),
                registerDTO.firstName(),
                registerDTO.lastName(),
                registerDTO.email(),
                registerDTO.phoneNumber(),
                registerDTO.language(),
                registerDTO.avatar());

        return ResponseEntity.created(URI.create(accountCreatedUrl + clientAccount.getId())).build();
    }

    @Override
    public ResponseEntity<?> registerStaff(AccountRegisterDTO registerDTO) {
        Account staffAccount = this.accountService.createStaffAccount(registerDTO.login(),
                passwordEncoder.encode(registerDTO.password()),
                registerDTO.firstName(),
                registerDTO.lastName(),
                registerDTO.email(),
                registerDTO.phoneNumber(),
                registerDTO.language(),
                registerDTO.avatar());

        return ResponseEntity.created(URI.create(accountCreatedUrl + staffAccount.getId())).build();
    }

    @Override
    public ResponseEntity<?> registerAdmin(AccountRegisterDTO registerDTO) {
        Account adminAccount = this.accountService.createAdminAccount(registerDTO.login(),
                passwordEncoder.encode(registerDTO.password()),
                registerDTO.firstName(),
                registerDTO.lastName(),
                registerDTO.email(),
                registerDTO.phoneNumber(),
                registerDTO.language(),
                registerDTO.avatar());

        return ResponseEntity.created(URI.create(accountCreatedUrl + adminAccount.getId())).build();
    }
}
