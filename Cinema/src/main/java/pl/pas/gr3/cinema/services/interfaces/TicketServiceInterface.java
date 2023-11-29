package pl.pas.gr3.cinema.services.interfaces;

import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    Response create(String movieTime, UUID clientID, UUID movieID, String ticketType);

    // Update methods

    Response delete(UUID ticketID);
}
