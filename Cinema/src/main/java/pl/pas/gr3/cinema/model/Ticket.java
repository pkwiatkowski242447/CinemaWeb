package pl.pas.gr3.cinema.model;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.pas.gr3.cinema.exceptions.model.MovieNullReferenceException;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.exceptions.model.TicketTypeNullReferenceException;
import pl.pas.gr3.cinema.model.users.Client;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Ticket {

    private final UUID ticketID;

    private LocalDateTime movieTime;

    private final double ticketFinalPrice;

    private final Client client;

    private final Movie movie;

    // Constructors

    public Ticket(UUID ticketID, LocalDateTime movieTime, Client client, Movie movie, TicketType ticketType)
    throws TicketCreateException {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.client = client;
        this.movie = movie;
        try {
            movie.setNumberOfAvailableSeats(movie.getNumberOfAvailableSeats() - 1);
        } catch (NullPointerException exception) {
            throw new MovieNullReferenceException("Reference to movie object is null.");
        }
        try {
            switch (ticketType) {
                case REDUCED: {
                    this.ticketFinalPrice = movie.getMovieBasePrice() * 0.75;
                    break;
                }
                default: {
                    this.ticketFinalPrice = movie.getMovieBasePrice();
                }
            }
        } catch (NullPointerException exception) {
            throw new TicketTypeNullReferenceException("Reference to ticket type object is null found.");
        }
    }

    public Ticket(UUID ticketID, LocalDateTime movieTime, double ticketFinalPrice, Client client, Movie movie) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.client = client;
        this.movie = movie;
    }

    // Equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return new EqualsBuilder()
                .append(ticketID, ticket.ticketID)
                .append(movieTime, ticket.movieTime)
                .append(ticketFinalPrice, ticket.ticketFinalPrice)
                .append(client, ticket.client)
                .append(movie, ticket.movie)
                .isEquals();
    }

    // HashCode method

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(ticketID)
                .append(movieTime)
                .append(ticketFinalPrice)
                .append(client)
                .append(movie)
                .toHashCode();
    }


    // ToString method

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Ticket ID: ", ticketID)
                .append("Movie time: ", movieTime)
                .append("Ticket final price: ", ticketFinalPrice)
                .append("Client: ", client.toString())
                .append("Movie: ", movie.toString())
                .toString();
    }
}
