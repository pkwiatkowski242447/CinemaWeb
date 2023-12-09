package pl.pas.gr3.cinema.mapping.mappers;

import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.mappers.users.AdminMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.ClientMapper;
import pl.pas.gr3.cinema.mapping.mappers.users.StaffMapper;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;

public class TicketMapper {

    public static TicketDoc toTicketDoc(Ticket ticket) {
        TicketDoc ticketDoc = new TicketDoc();
        ticketDoc.setTicketID(ticket.getTicketID());
        ticketDoc.setMovieTime(ticket.getMovieTime());
        ticketDoc.setTicketFinalPrice(ticket.getTicketFinalPrice());
        if (ticket.getClient() != null) {
            ticketDoc.setClientID(ticket.getClient().getClientID());
        } else {
            ticketDoc.setClientID(null);
        }
        if (ticket.getMovie() != null) {
            ticketDoc.setMovieID(ticket.getMovie().getMovieID());
        } else {
            ticketDoc.setMovieID(null);
        }
        return ticketDoc;
    }

    public static Ticket toTicket(TicketDoc ticketDoc, ClientDoc clientDoc, MovieDoc movieDoc) {
        Client client;
        if (clientDoc.getClass().equals(ClientDoc.class)) {
            client = ClientMapper.toClient(clientDoc);
        } else if (clientDoc.getClass().equals(AdminDoc.class)) {
            client = AdminMapper.toAdmin(clientDoc);
        } else {
            client = StaffMapper.toStaff(clientDoc);
        }
        return new Ticket(ticketDoc.getTicketID(),
                ticketDoc.getMovieTime(),
                ticketDoc.getTicketFinalPrice(),
                client,
                MovieMapper.toMovie(movieDoc));
    }
}
