package pl.pas.gr3.mvc.dto;

import lombok.Data;
import pl.pas.gr3.dto.output.MovieDTO;
import pl.pas.gr3.dto.output.TicketDTO;
import pl.pas.gr3.dto.output.ClientDTO;

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
