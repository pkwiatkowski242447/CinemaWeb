package pl.pas.gr3.cinema.dto.json;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketJson {

    @JsonbProperty("id")
    private UUID ticketID;

    @JsonbProperty("time")
    private LocalDateTime movieTime;

    @JsonbProperty("price")
    private double ticketFinalPrice;

    @JsonbProperty("client-id")
    private UUID clientID;

    @JsonbProperty("movie-id")
    private UUID movieID;

    @JsonbCreator
    public TicketJson(@JsonbProperty("id") UUID ticketID,
                      @JsonbProperty("time") LocalDateTime movieTime,
                      @JsonbProperty("price") double ticketFinalPrice,
                      @JsonbProperty("client-id") UUID clientID,
                      @JsonbProperty("movie-id") UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
