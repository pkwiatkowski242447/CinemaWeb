package pl.pas.gr3.mvc.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.model.Ticket;
import pl.pas.gr3.mvc.model.User;
import pl.pas.gr3.mvc.model.Movie;

@Data
public class TicketWithUserAndMovie {

    private TicketDTO ticket;
    private ClientDTO client;
    private MovieDTO movie;

    // Constructors

    public TicketWithUserAndMovie() {
    }

    public TicketWithUserAndMovie(TicketDTO ticket, ClientDTO client, MovieDTO movie) {
        this.ticket = ticket;
        this.client = client;
        this.movie = movie;
    }
}
