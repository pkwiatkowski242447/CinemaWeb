package pl.pas.gr3.cinema.services.interfaces;

import pl.pas.gr3.cinema.exceptions.services.GeneralTicketServiceException;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketManagerInterface extends ManagerInterface<Ticket> {

    // Create methods

    Ticket create(String movieTime, UUID clientID, UUID movieID, String ticketType) throws GeneralTicketServiceException;

    // Update methods

    void update(Ticket ticket) throws GeneralTicketServiceException;

    // Delete methods

    void delete(UUID ticketID) throws GeneralTicketServiceException;
}
