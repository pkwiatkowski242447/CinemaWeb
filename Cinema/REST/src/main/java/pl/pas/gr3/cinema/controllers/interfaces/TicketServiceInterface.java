package pl.pas.gr3.cinema.controllers.interfaces;

import jakarta.ws.rs.core.Response;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    Response create(TicketInputDTO ticketInputDTO);

    // Update methods

    Response delete(UUID ticketID);

    // Update methods
    Response update(TicketDTO ticketDTO);
}
