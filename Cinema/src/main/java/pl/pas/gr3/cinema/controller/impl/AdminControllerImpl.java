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
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.UpdateAccountRequest;
import pl.pas.gr3.cinema.service.impl.AdminServiceImpl;
import pl.pas.gr3.cinema.entity.account.Admin;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

    private final AdminServiceImpl adminService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findById(UUID adminId) {
        Admin admin = adminService.findByUUID(adminId);
        AccountResponse outputDto = accountMapper.toResponse(admin);
        return ResponseEntity.ok(outputDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findByLogin(String adminLogin) {
        Admin admin = adminService.findByLogin(adminLogin);
        AccountResponse outputDto = accountMapper.toResponse(admin);
        return ResponseEntity.ok(outputDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findSelfByLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminService.findByLogin(auth.getName());
        AccountResponse outputDto = accountMapper.toResponse(admin);

        String signature = jwsService.generateSignatureForUser(admin);
        return ResponseEntity.ok().eTag(signature).body(outputDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(String adminLogin) {
        List<Admin> foundAdmins = adminService.findAllMatchingLogin(adminLogin);
        List<AccountResponse> outputDtos = foundAdmins.stream().map(accountMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<Admin> admins = adminService.findAll();
        List<AccountResponse> outputDtos = admins.stream().map(accountMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateAccountRequest userUpdateDto) {
        String password = userUpdateDto.password() == null ? null : passwordEncoder.encode(userUpdateDto.password());
        Admin admin = new Admin(userUpdateDto.id(), userUpdateDto.login(), password, userUpdateDto.active());

        String ifMatchContent = ifMatch.replace("\"", "");
        if (!jwsService.verifyUserSignature(ifMatchContent, admin))
            throw new ApplicationDataIntegrityCompromisedException();

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
