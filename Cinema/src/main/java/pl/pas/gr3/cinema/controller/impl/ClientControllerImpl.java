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
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.service.impl.JWSService;
import pl.pas.gr3.cinema.dto.account.AccountResponse;
import pl.pas.gr3.cinema.dto.account.UpdateAccountRequest;
import pl.pas.gr3.cinema.dto.ticket.TicketResponse;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Client;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientControllerImpl implements ClientController {

    private final AccountService<Client> clientService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;
    private final TicketMapper ticketMapper;

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<Client> foundClients = clientService.findAll();
        List<AccountResponse> accountResponses = foundClients.stream().map(accountMapper::toResponse).toList();
        return accountResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(accountResponses);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findById(UUID clientId) {
        Client client = clientService.findByUUID(clientId);
        AccountResponse accountResponse = accountMapper.toResponse(client);
        return ResponseEntity.ok(accountResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findByLogin(String clientLogin) {
        Client client = clientService.findByLogin(clientLogin);
        AccountResponse accountResponse = accountMapper.toResponse(client);
        return ResponseEntity.ok(accountResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<AccountResponse> findSelfByLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Client client = clientService.findByLogin(auth.getName());
        AccountResponse accountResponse = accountMapper.toResponse(client);

        String signature = jwsService.generateSignature(accountResponse);
        return ResponseEntity.ok().eTag(signature).body(accountResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Override
    public ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(String clientLogin) {
        List<Client> clients = clientService.findAllMatchingLogin(clientLogin);
        List<AccountResponse> accountResponses = clients.stream().map(accountMapper::toResponse).toList();
        return accountResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(accountResponses);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketResponse>> getTicketsForCertainUser(UUID clientId) {
        List<Ticket> clientTickets = clientService.getTicketsForClient(clientId);
        List<TicketResponse> ticketResponses = clientTickets.stream().map(ticketMapper::toResponse).toList();
        return ticketResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ticketResponses);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<List<TicketResponse>> getTicketsForCertainUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Client client = clientService.findByLogin(auth.getName());
        List<Ticket> clientTickets = clientService.getTicketsForClient(client.getId());
        List<TicketResponse> ticketResponses = clientTickets.stream().map(ticketMapper::toResponse).toList();
        return ticketResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ticketResponses);
    }

    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateAccountRequest request) {
        String signature = jwsService.generateSignature(request);
        if (!signature.equals(ifMatch)) throw new ApplicationDataIntegrityCompromisedException();

        String password = request.getPassword() == null ? null : passwordEncoder.encode(request.getPassword());
        Client client = new Client(request.getId(), request.getLogin(), password, request.isActive());

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
