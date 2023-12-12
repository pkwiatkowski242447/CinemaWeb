package pl.pas.gr3.cinema.controllers.interfaces;

import org.springframework.http.ResponseEntity;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface TicketServiceInterface extends ServiceInterface<Ticket> {

    // Create methods

    ResponseEntity<?> create(TicketInputDTO ticketInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID ticketID);

    // Update methods
    ResponseEntity<?> update(TicketDTO ticketDTO);
}
