package pl.pas.gr3.cinema.repository.api;

import pl.pas.gr3.cinema.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends Repository<Ticket> {

    /* CREATE */

    Ticket create(LocalDateTime movieTime, UUID clientId, UUID movieId);

    /* READ */

    List<Ticket> findAll();

    /* UPDATE */

    void update(Ticket ticket);

    /* DELETE */

    void delete(UUID ticketId);
}