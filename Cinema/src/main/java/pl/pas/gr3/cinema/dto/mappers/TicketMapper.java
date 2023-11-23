package pl.pas.gr3.cinema.dto.mappers;

import pl.pas.gr3.cinema.dto.json.MovieJson;
import pl.pas.gr3.cinema.dto.json.TicketJson;
import pl.pas.gr3.cinema.dto.json.users.ClientJson;
import pl.pas.gr3.cinema.model.Ticket;

public class TicketMapper {

    public static TicketJson toTicketJsonFromTicket(Ticket ticket) {
        TicketJson ticketJson = new TicketJson();
        ticketJson.setTicketID(ticket.getTicketID());
        ticketJson.setMovieTime(ticket.getMovieTime());
        ticketJson.setTicketFinalPrice(ticket.getTicketFinalPrice());
        ticketJson.setClientID(ticket.getClient().getClientID());
        ticketJson.setMovieID(ticket.getMovie().getMovieID());
        return ticketJson;
    }
/*
    public static Ticket toTicketFromTicketJson(TicketJson ticketJson, ClientJson clientJson, MovieJson movieJson) {
        return new Ticket(ticketJson.getTicketID(),
                ticketJson.getMovieTime(),
                ticketJson.getTicketFinalPrice(),
                clientJson.getClientID(),
                movieJson.getMovieID());
    }

 */
}
