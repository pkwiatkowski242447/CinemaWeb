package pl.pas.gr3.cinema.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TicketInputDTO {

    @JsonbProperty("movie-time")
    private String movieTime;

    @JsonbProperty("client-id")
    private UUID clientID;

    @JsonbProperty("movie-id")
    private UUID movieID;

    @JsonbProperty("ticket-type")
    private String ticketType;

    @JsonbCreator
    public TicketInputDTO(@JsonbProperty("movie-title") String movieTime,
                          @JsonbProperty("client-id") UUID clientID,
                          @JsonbProperty("movie-id") UUID movieID,
                          @JsonbProperty("ticket-type") String ticketType) {
        this.movieTime = movieTime;
        this.clientID = clientID;
        this.movieID = movieID;
        this.ticketType = ticketType;
    }
}
