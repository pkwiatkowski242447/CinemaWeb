package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.exceptions.repositories.TicketRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepositoryInterface extends RepositoryInterface<Ticket> {

    // Create methods

    Ticket create(LocalDateTime movieTime, UUID clientID, UUID movieID) throws TicketRepositoryException;

    // Read methods

    List<Ticket> findAll() throws TicketRepositoryException;

    // Update methods

    void update(Ticket ticket) throws TicketRepositoryException;

    // Delete methods

    void delete(UUID ticketID) throws TicketRepositoryException;
}
