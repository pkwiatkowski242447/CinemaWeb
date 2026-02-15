package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.ResponseEntity;
import pl.pas.gr3.cinema.dto.input.TicketInputDTO;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.UUID;

public interface ITicketService extends IService<Ticket> {

    // Create methods

    ResponseEntity<?> create(TicketInputDTO ticketInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID ticketID);
}
