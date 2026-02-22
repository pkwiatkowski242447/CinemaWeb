package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.controller.api.AuthenticationController;
import pl.pas.gr3.cinema.dto.LoginResponse;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.service.api.AuthenticationService;
import pl.pas.gr3.cinema.service.impl.JWTService;
import pl.pas.gr3.cinema.dto.account.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.account.AccountResponse;

import java.net.URI;
import java.text.MessageFormat;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {
    
    private final AuthenticationService authenticationService;
    private final JWTService jwtService;

    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> registerClient(LoginAccountRequest registerRequest) {
        Client client = authenticationService.registerClient(registerRequest.login(), registerRequest.password());
        AccountResponse accountResponse = new AccountResponse(client.getId(), client.getLogin(), client.isActive());
        String location = MessageFormat.format("http://localhost:8000/api/v1/clients/{0}", accountResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(accountResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> registerAdmin(@RequestBody LoginAccountRequest registerRequest) {
        Admin admin = authenticationService.registerAdmin(registerRequest.login(), registerRequest.password());
        AccountResponse accountResponse = new AccountResponse(admin.getId(), admin.getLogin(), admin.isActive());
        String location = MessageFormat.format("http://localhost:8000/api/v1/admins/{0}", accountResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(accountResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> registerStaff(LoginAccountRequest registerRequest) {
        Staff staff = authenticationService.registerStaff(registerRequest.login(), registerRequest.password());
        AccountResponse accountResponse = new AccountResponse(staff.getId(), staff.getLogin(), staff.isActive());
        String location = MessageFormat.format("http://localhost:8000/api/v1/staffs/{0}", accountResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(accountResponse);
    }

    @PreAuthorize("isAnonymous()")
    @Override
    public ResponseEntity<LoginResponse> loginClient(LoginAccountRequest loginRequest) {
        Client client = authenticationService.loginClient(loginRequest.login(), loginRequest.password());
        String accessToken = jwtService.generateJWTToken(client);
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    @PreAuthorize("isAnonymous()")
    @Override
    public ResponseEntity<LoginResponse> loginAdmin(LoginAccountRequest loginRequest) {
        Admin admin = authenticationService.loginAdmin(loginRequest.login(), loginRequest.password());
        String accessToken = jwtService.generateJWTToken(admin);
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    @PreAuthorize("isAnonymous()")
    @Override
    public ResponseEntity<LoginResponse> loginStaff(LoginAccountRequest loginRequest) {
        Staff staff = authenticationService.loginStaff(loginRequest.login(), loginRequest.password());
        String accessToken = jwtService.generateJWTToken(staff);
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    @PreAuthorize("isAnonymous() or isAuthenticated()")
    @Override
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
