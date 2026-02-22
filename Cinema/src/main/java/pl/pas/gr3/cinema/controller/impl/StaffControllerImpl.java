package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.controller.api.StaffController;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.service.impl.JWSService;
import pl.pas.gr3.cinema.dto.account.AccountResponse;
import pl.pas.gr3.cinema.dto.account.UpdateAccountRequest;
import pl.pas.gr3.cinema.entity.account.Staff;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StaffControllerImpl implements StaffController {

    private final AccountService<Staff> staffService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findById(UUID staffId) {
        Staff staff = staffService.findByUUID(staffId);
        AccountResponse accountResponse = accountMapper.toResponse(staff);
        return ResponseEntity.ok(accountResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findByLogin(String staffLogin) {
        Staff staff = staffService.findByLogin(staffLogin);
        AccountResponse accountResponse = accountMapper.toResponse(staff);
        return ResponseEntity.ok(accountResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF')")
    @Override
    public ResponseEntity<AccountResponse> findSelfByLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Staff staff = staffService.findByLogin(auth.getName());
        AccountResponse accountResponse = accountMapper.toResponse(staff);

        String signature = jwsService.generateSignature(accountResponse);
        return ResponseEntity.ok().eTag(signature).body(accountResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(String staffLogin) {
        List<Staff> foundStaff = staffService.findAllMatchingLogin(staffLogin);
        List<AccountResponse> accountResponses = foundStaff.stream().map(accountMapper::toResponse).toList();
        return accountResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(accountResponses);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<Staff> foundStaff = staffService.findAll();
        List<AccountResponse> accountResponses = foundStaff.stream().map(accountMapper::toResponse).toList();
        return accountResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(accountResponses);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateAccountRequest request) {
        String signature = jwsService.generateSignature(request);
        if (!signature.equals(ifMatch)) throw new ApplicationDataIntegrityCompromisedException();

        String password = request.getPassword() == null ? null : passwordEncoder.encode(request.getPassword());
        Staff staff = new Staff(request.getId(), request.getLogin(), password, request.isActive());

        staffService.update(staff);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> activate(UUID staffID) {
        staffService.activate(staffID);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> deactivate(UUID staffID) {
        staffService.deactivate(staffID);
        return ResponseEntity.noContent().build();
    }
}
