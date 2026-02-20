package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.exception.forbidden.AccessDeniedException;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.mapper.TicketMapper;
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.service.api.TicketService;
import pl.pas.gr3.cinema.dto.input.CreateOwnTicketRequest;
import pl.pas.gr3.cinema.dto.output.TicketResponse;
import pl.pas.gr3.cinema.dto.input.CreateTicketRequest;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.controller.api.TicketController;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TicketControllerImpl implements TicketController {

    private final TicketService ticketService;
    private final AccountService<Client> clientService;
    private final JWSService jwsService;

    private final TicketMapper ticketMapper;

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<TicketResponse> create(CreateTicketRequest createTicketRequest) {
        Client client = clientService.findByUUID(createTicketRequest.getClientId());
        Ticket ticket = ticketService.create(createTicketRequest.getMovieTime(), client.getId(), createTicketRequest.getMovieId());
        TicketResponse ticketResponse = ticketMapper.toResponse(ticket);

        String location = MessageFormat.format("http://localhost:8000/api/v1/tickets/{0}", ticketResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(ticketResponse);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<TicketResponse> create(CreateOwnTicketRequest createOwnTicketRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException();

        Client client = clientService.findByLogin(auth.getName());
        Ticket ticket = ticketService.create(createOwnTicketRequest.getMovieTime(), client.getId(), createOwnTicketRequest.getMovieId());
        TicketResponse ticketResponse = ticketMapper.toResponse(ticket);

        String location = MessageFormat.format("http://localhost:8000/api/v1/tickets/{0}", ticketResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(ticketResponse);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'CLIENT')")
    @Override
    public ResponseEntity<TicketResponse> findById(UUID ticketId) {
        Ticket ticket = ticketService.findByUUID(ticketId);
        TicketResponse ticketResponse = ticketMapper.toResponse(ticket);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT"))) {
            String signature = jwsService.generateSignatureForTicket(ticket);
            return ResponseEntity.ok().eTag(signature).body(ticketResponse);
        }

        return ResponseEntity.ok(ticketResponse);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketResponse>> findAll() {
        List<Ticket> listOfFoundTickets = this.ticketService.findAll();
        List<TicketResponse> outputDtos = listOfFoundTickets.stream().map(ticketMapper::toResponse).toList();
        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, TicketResponse ticketResponse) {
        Ticket ticket = ticketService.findByUUID(ticketResponse.getId());

        String ifMatchContent = ifMatch.replace("\"", "");
        if (!jwsService.verifyTicketSignature(ifMatchContent, ticket))
            throw new ApplicationDataIntegrityCompromisedException();

        ticket.setMovieTime(ticketResponse.getMovieTime());

        ticketService.update(ticket);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<Void> delete(UUID ticketId) {
        ticketService.delete(ticketId);
        return ResponseEntity.noContent().build();
    }
}
