package pl.pas.gr3.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.exception.bad_request.TicketCreateException;
import pl.pas.gr3.cinema.exception.forbidden.AccessDeniedException;
import pl.pas.gr3.cinema.repository.api.AccountRepository;
import pl.pas.gr3.cinema.repository.api.TicketRepository;
import pl.pas.gr3.cinema.service.api.TicketService;
import pl.pas.gr3.cinema.entity.Ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;

    @Override
    public Ticket create(String movieTime, UUID clientId, UUID movieId) {
        try {
            LocalDateTime movieTimeParsed = LocalDateTime.parse(movieTime);
            return ticketRepository.create(movieTimeParsed, clientId, movieId);
        } catch (DateTimeParseException exception) {
            throw new TicketCreateException(exception);
        }
    }

    @Override
    public Ticket findByUUID(UUID ticketId) {
        Ticket foundTicket = ticketRepository.findByUUID(ticketId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("CLIENT"))) {
            Client foundClient = accountRepository.findClientByLogin(auth.getName());
            if (!foundTicket.getUserId().equals(foundClient.getId())) throw new AccessDeniedException();
        }

        return ticketRepository.findByUUID(ticketId);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public void update(Ticket ticket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Client foundClient = accountRepository.findClientByLogin(auth.getName());

        if (!ticket.getUserId().equals(foundClient.getId()))
            throw new AccessDeniedException();

        ticketRepository.update(ticket);
    }

    @Override
    public void delete(UUID ticketId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Client foundClient = accountRepository.findClientByLogin(auth.getName());
        Ticket ticket = ticketRepository.findByUUID(ticketId);

        if (!ticket.getUserId().equals(foundClient.getId()))
            throw new AccessDeniedException();

        ticketRepository.delete(ticketId);
    }
}
