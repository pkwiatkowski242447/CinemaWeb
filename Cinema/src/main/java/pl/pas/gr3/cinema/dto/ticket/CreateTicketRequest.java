package pl.pas.gr3.cinema.dto.ticket;


import java.util.UUID;

public record CreateTicketRequest(
    String movieTime,
    UUID clientId,
    UUID movieId
) {}
