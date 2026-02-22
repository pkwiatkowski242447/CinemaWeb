package pl.pas.gr3.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.exception.bad_request.AccountNotActiveException;
import pl.pas.gr3.cinema.repository.api.AccountRepository;
import pl.pas.gr3.cinema.service.api.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public Client registerClient(String login, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return accountRepository.createClient(login, encodedPassword);
    }

    @Override
    public Admin registerAdmin(String login, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return accountRepository.createAdmin(login, encodedPassword);
    }

    @Override
    public Staff registerStaff(String login, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return accountRepository.createStaff(login, encodedPassword);
    }

    @Override
    public Client loginClient(String login, String password) {
        Client client = accountRepository.findClientByLogin(login);
        if (!client.isActive()) throw new AccountNotActiveException();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        return client;
    }

    @Override
    public Admin loginAdmin(String login, String password) {
        Admin admin = accountRepository.findAdminByLogin(login);
        if (!admin.isActive()) throw new AccountNotActiveException();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        return admin;
    }

    @Override
    public Staff loginStaff(String login, String password) {
        Staff staff = accountRepository.findStaffByLogin(login);
        if (!staff.isActive()) throw new AccountNotActiveException();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        return staff;
    }
}
