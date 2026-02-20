package pl.pas.gr3.cinema.service.api;

import pl.pas.gr3.cinema.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    /* CREATE */

    Ticket create(String movieTime, UUID clientId, UUID movieId);

    /* READ */

    Ticket findByUUID(UUID ticketId);
    List<Ticket> findAll();

    /* UPDATE */

    void update(Ticket ticket);

    /* DELETE */

    void delete(UUID ticketId);
}
