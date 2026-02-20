package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.controller.api.ClientController;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.mapper.TicketMapper;
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.UpdateAccountRequest;
import pl.pas.gr3.cinema.dto.output.TicketResponse;
import pl.pas.gr3.cinema.service.impl.ClientServiceImpl;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Client;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientControllerImpl implements ClientController {

    private final ClientServiceImpl clientService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;
    private final TicketMapper ticketMapper;

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<Client> foundClients = clientService.findAll();
        List<AccountResponse> outputDtos = foundClients.stream().map(accountMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findById(UUID clientId) {
        Client client = clientService.findByUUID(clientId);
        AccountResponse outputDto = accountMapper.toResponse(client);
        return ResponseEntity.ok(outputDto);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findByLogin(String clientLogin) {
        Client client = clientService.findByLogin(clientLogin);
        AccountResponse outputDto = accountMapper.toResponse(client);
        return ResponseEntity.ok(outputDto);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findSelfByLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Client client = clientService.findByLogin(auth.getName());
        AccountResponse outputDto = accountMapper.toResponse(client);

        String signature = jwsService.generateSignatureForUser(client);
        return ResponseEntity.ok().eTag(signature).body(outputDto);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(String clientLogin) {
        List<Client> clients = clientService.findAllMatchingLogin(clientLogin);
        List<AccountResponse> outputDtos = clients.stream().map(accountMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketResponse>> getTicketsForCertainUser(UUID clientId) {
        List<Ticket> clientTickets = clientService.getTicketsForClient(clientId);
        List<TicketResponse> outputDtos = clientTickets.stream().map(ticketMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<List<TicketResponse>> getTicketsForCertainUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Client client = clientService.findByLogin(auth.getName());
        List<Ticket> clientTickets = clientService.getTicketsForClient(client.getId());
        List<TicketResponse> outputDtos = clientTickets.stream().map(ticketMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateAccountRequest userUpdateDto) {
        String password = userUpdateDto.password() == null ? null : passwordEncoder.encode(userUpdateDto.password());
        Client client = new Client(userUpdateDto.id(), userUpdateDto.login(), password, userUpdateDto.active());

        String ifMatchContent = ifMatch.replace("\"", "");
        if (!jwsService.verifyUserSignature(ifMatchContent, client))
            throw new ApplicationDataIntegrityCompromisedException();

        clientService.update(client);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> activate(UUID clientId) {
        clientService.activate(clientId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> deactivate(UUID clientId) {
        clientService.deactivate(clientId);
        return ResponseEntity.noContent().build();
    }
}
