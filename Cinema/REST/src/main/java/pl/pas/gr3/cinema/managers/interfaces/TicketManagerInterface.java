package pl.pas.gr3.cinema.managers.interfaces;

import pl.pas.gr3.cinema.exceptions.managers.GeneralTicketManagerException;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketManagerInterface extends ManagerInterface<Ticket> {

    // Create methods

    Ticket create(String movieTime, UUID clientID, UUID movieID, String ticketType) throws GeneralTicketManagerException;

    // Update methods

    void update(Ticket ticket) throws GeneralTicketManagerException;

    // Delete methods

    void delete(UUID ticketID) throws GeneralTicketManagerException;
}
