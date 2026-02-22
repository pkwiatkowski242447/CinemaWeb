package pl.pas.gr3.cinema.dto.ticket;

import java.util.UUID;

public record CreateOwnTicketRequest(
    String movieTime,
    UUID movieId
) {}
