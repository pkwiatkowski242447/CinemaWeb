package pl.pas.gr3.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketInputDTO {

    @JsonProperty("movie-time")
    @JsonbProperty("movie-time")
    private String movieTime;

    @JsonProperty("client-id")
    @JsonbProperty("client-id")
    private UUID clientID;

    @JsonProperty("movie-id")
    @JsonbProperty("movie-id")
    private UUID movieID;

    @JsonProperty("ticket-type")
    @JsonbProperty("ticket-type")
    private String ticketType;

    @JsonCreator
    @JsonbCreator
    public TicketInputDTO(@JsonProperty("movie-time") @JsonbProperty("movie-time") String movieTime,
                          @JsonProperty("client-id") @JsonbProperty("client-id") UUID clientID,
                          @JsonProperty("movie-id") @JsonbProperty("movie-id") UUID movieID,
                          @JsonProperty("ticket-type") @JsonbProperty("ticket-type") String ticketType) {
        this.movieTime = movieTime;
        this.clientID = clientID;
        this.movieID = movieID;
        this.ticketType = ticketType;
    }
}
