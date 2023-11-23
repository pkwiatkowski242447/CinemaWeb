package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepositoryInterface extends RepositoryInterface<Ticket> {

    // Create methods

    Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID, TicketType ticketType);

    // Other methods
    List<Ticket> findAllTickets();
}
