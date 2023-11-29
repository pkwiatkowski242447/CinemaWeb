package pl.pas.gr3.cinema.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketDTO {

    @JsonbProperty("ticket-id")
    private UUID ticketID;

    @JsonbProperty("movie-time")
    private LocalDateTime movieTime;

    @JsonbProperty("ticket-final-price")
    private double ticketFinalPrice;

    @JsonbProperty("client-id")
    private UUID clientID;

    @JsonbProperty("movie-id")
    private UUID movieID;

    @JsonbCreator
    public TicketDTO(@JsonbProperty("ticket-id") UUID ticketID,
                     @JsonbProperty("movie-time") LocalDateTime movieTime,
                     @JsonbProperty("ticket-final-price") double ticketFinalPrice,
                     @JsonbProperty("client-id") UUID clientID,
                     @JsonbProperty("movie-id") UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
