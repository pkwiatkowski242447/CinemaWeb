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
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.UpdateAccountRequest;
import pl.pas.gr3.cinema.service.impl.StaffServiceImpl;
import pl.pas.gr3.cinema.entity.account.Staff;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StaffControllerImpl implements StaffController {

    private final StaffServiceImpl staffService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findById(UUID staffID) {
        Staff staff = staffService.findByUUID(staffID);
        AccountResponse outputDto = accountMapper.toResponse(staff);
        return ResponseEntity.ok(outputDto);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findByLogin(String staffLogin) {
        Staff staff = staffService.findByLogin(staffLogin);
        AccountResponse outputDto = accountMapper.toResponse(staff);
        return ResponseEntity.ok(outputDto);
    }

    @PreAuthorize("hasAnyRole('STAFF')")
    @Override
    public ResponseEntity<AccountResponse> findSelfByLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Staff staff = staffService.findByLogin(auth.getName());
        AccountResponse outputDto = accountMapper.toResponse(staff);

        String signature = jwsService.generateSignatureForUser(staff);
        return ResponseEntity.ok().eTag(signature).body(outputDto);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(String staffLogin) {
        List<Staff> foundStaff = staffService.findAllMatchingLogin(staffLogin);
        List<AccountResponse> outputDtos = foundStaff.stream().map(accountMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<Staff> foundStaff = staffService.findAll();
        List<AccountResponse> outputDtos = foundStaff.stream().map(accountMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateAccountRequest userUpdateDto) {
        String password = userUpdateDto.password() == null ? null : passwordEncoder.encode(userUpdateDto.password());
        Staff staff = new Staff(userUpdateDto.id(), userUpdateDto.login(), password, userUpdateDto.active());

        String ifMatchContent = ifMatch.replace("\"", "");
        if (!jwsService.verifyUserSignature(ifMatchContent, staff))
            throw new ApplicationDataIntegrityCompromisedException();

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
