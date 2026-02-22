package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.controller.api.AdminController;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.service.impl.JWSService;
import pl.pas.gr3.cinema.dto.account.AccountResponse;
import pl.pas.gr3.cinema.dto.account.UpdateAccountRequest;
import pl.pas.gr3.cinema.entity.account.Admin;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

    private final AccountService<Admin> adminService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findById(UUID adminId) {
        Admin admin = adminService.findByUUID(adminId);
        AccountResponse outputaccountResponse = accountMapper.toResponse(admin);
        return ResponseEntity.ok(outputaccountResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findByLogin(String adminLogin) {
        Admin admin = adminService.findByLogin(adminLogin);
        AccountResponse accountResponse = accountMapper.toResponse(admin);
        return ResponseEntity.ok(accountResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findSelfByLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminService.findByLogin(auth.getName());
        AccountResponse accountResponse = accountMapper.toResponse(admin);

        String signature = jwsService.generateSignature(accountResponse);
        return ResponseEntity.ok().eTag(signature).body(accountResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(String adminLogin) {
        List<Admin> foundAdmins = adminService.findAllMatchingLogin(adminLogin);
        List<AccountResponse> accountResponses = foundAdmins.stream().map(accountMapper::toResponse).toList();
        return accountResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(accountResponses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<Admin> admins = adminService.findAll();
        List<AccountResponse> accountResponses = admins.stream().map(accountMapper::toResponse).toList();
        return accountResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(accountResponses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateAccountRequest request) {
        String signature = jwsService.generateSignature(request);
        if (!signature.equals(ifMatch)) throw new ApplicationDataIntegrityCompromisedException();

        String password = request.getPassword() == null ? null : passwordEncoder.encode(request.getPassword());
        Admin admin = new Admin(request.getId(), request.getLogin(), password, request.isActive());

        adminService.update(admin);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> activate(UUID adminId) {
        this.adminService.activate(adminId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> deactivate(UUID adminId) {
        this.adminService.deactivate(adminId);
        return ResponseEntity.noContent().build();
    }
}
