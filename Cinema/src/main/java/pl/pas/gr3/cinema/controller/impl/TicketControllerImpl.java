package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.pas.gr3.cinema.exception.forbidden.AccessDeniedException;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.service.impl.ClientServiceImpl;
import pl.pas.gr3.cinema.dto.input.TicketSelfInputDTO;
import pl.pas.gr3.cinema.dto.output.TicketDTO;
import pl.pas.gr3.cinema.dto.input.TicketInputDTO;
import pl.pas.gr3.cinema.service.impl.TicketServiceImpl;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.controller.api.TicketController;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TicketControllerImpl implements TicketController {

    private final TicketServiceImpl ticketService;
    private final ClientServiceImpl clientService;
    private final JWSService jwsService;

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<TicketDTO> create(TicketInputDTO ticketInputDto) {
        Client client = clientService.findByUUID(ticketInputDto.getClientId());
        Ticket ticket = ticketService.create(ticketInputDto.getMovieTime(), client.getId(), ticketInputDto.getMovieId());

        TicketDTO ticketDto = new TicketDTO(ticket.getId(), ticket.getMovieTime(), ticket.getPrice(), ticket.getUserId(), ticket.getMovieId());

        String location = MessageFormat.format("http://localhost:8000/api/v1/tickets/{0}", ticketDto.getId());
        return ResponseEntity.created(URI.create(location)).body(ticketDto);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<TicketDTO> create(TicketSelfInputDTO ticketSelfInputDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException();

        Client client = clientService.findByLogin(auth.getName());
        Ticket ticket = ticketService.create(ticketSelfInputDto.getMovieTime(), client.getId(), ticketSelfInputDto.getMovieId());

        TicketDTO ticketDto = new TicketDTO(ticket.getId(), ticket.getMovieTime(), ticket.getPrice(), ticket.getUserId(), ticket.getMovieId());

        String location = MessageFormat.format("http://localhost:8000/api/v1/tickets/{0}", ticketDto.getId());
        return ResponseEntity.created(URI.create(location)).body(ticketDto);
    }

    @PreAuthorize("hasAnyRole('STAFF', 'CLIENT')")
    @Override
    public ResponseEntity<TicketDTO> findById(UUID ticketId) {
        Ticket ticket = ticketService.findByUUID(ticketId);
        TicketDTO ticketDto = new TicketDTO(ticket.getId(), ticket.getMovieTime(), ticket.getPrice(), ticket.getUserId(), ticket.getMovieId());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT"))) {
            String signature = jwsService.generateSignatureForTicket(ticket);
            return ResponseEntity.ok().eTag(signature).body(ticketDto);
        }

        return ResponseEntity.ok(ticketDto);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketDTO>> findAll() {
        List<Ticket> listOfFoundTickets = this.ticketService.findAll();
        List<TicketDTO> outputDtos = listOfFoundTickets.stream().map(ticket ->
            new TicketDTO(ticket.getId(), ticket.getMovieTime(), ticket.getPrice(), ticket.getUserId(), ticket.getMovieId())
        ).toList();

        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, TicketDTO ticketDto) {
        Ticket ticket = ticketService.findByUUID(ticketDto.getId());

        String ifMatchContent = ifMatch.replace("\"", "");
        if (!jwsService.verifyTicketSignature(ifMatchContent, ticket))
            throw new ApplicationDataIntegrityCompromisedException();

        ticket.setMovieTime(ticketDto.getMovieTime());

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
