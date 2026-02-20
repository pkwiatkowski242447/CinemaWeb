package pl.pas.gr3.cinema.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketResponse {

    @JsonProperty("ticket-id")
    @JsonbProperty("ticket-id")
    private UUID id;

    @JsonProperty("movie-time")
    @JsonbProperty("movie-time")
    private LocalDateTime movieTime;

    @JsonProperty("ticket-final-price")
    @JsonbProperty("ticket-final-price")
    private double finalPrice;

    @JsonProperty("client-id")
    @JsonbProperty("client-id")
    private UUID clientId;

    @JsonProperty("movie-id")
    @JsonbProperty("movie-id")
    private UUID movieId;

    @JsonCreator
    @JsonbCreator
    public TicketResponse(@JsonProperty("ticket-id") @JsonbProperty("ticket-id") UUID id,
                          @JsonProperty("movie-time") @JsonbProperty("movie-time") LocalDateTime movieTime,
                          @JsonProperty("ticket-final-price") @JsonbProperty("ticket-final-price") double finalPrice,
                          @JsonProperty("client-id") @JsonbProperty("client-id") UUID clientId,
                          @JsonProperty("movie-id") @JsonbProperty("movie-id") UUID movieId) {
        this.id = id;
        this.movieTime = movieTime;
        this.finalPrice = finalPrice;
        this.clientId = clientId;
        this.movieId = movieId;
    }
}
