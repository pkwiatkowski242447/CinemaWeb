package pl.pas.gr3.cinema.services.api;

import pl.pas.gr3.cinema.exception.services.GeneralTicketServiceException;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    Ticket create(String movieTime, UUID clientID, UUID movieID) throws GeneralTicketServiceException;

    // Update methods

    void update(Ticket ticket) throws GeneralTicketServiceException;

    // Delete methods

    void delete(UUID ticketID) throws GeneralTicketServiceException;
}
