package pl.pas.gr3.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketDTO {

    @JsonProperty("ticket-id")
    private UUID ticketID;

    @JsonProperty("movie-time")
    private LocalDateTime movieTime;

    @JsonProperty("ticket-final-price")
    private double ticketFinalPrice;

    @JsonProperty("client-id")
    private UUID clientID;

    @JsonProperty("movie-id")
    private UUID movieID;

    @JsonCreator
    public TicketDTO(@JsonProperty("ticket-id") UUID ticketID,
                     @JsonProperty("movie-time") LocalDateTime movieTime,
                     @JsonProperty("ticket-final-price") double ticketFinalPrice,
                     @JsonProperty("client-id") UUID clientID,
                     @JsonProperty("movie-id") UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
