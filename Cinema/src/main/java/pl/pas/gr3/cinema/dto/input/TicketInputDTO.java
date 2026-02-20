package pl.pas.gr3.cinema.dto.input;

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
    private UUID clientId;

    @JsonProperty("movie-id")
    @JsonbProperty("movie-id")
    private UUID movieId;


    @JsonCreator
    @JsonbCreator
    public TicketInputDTO(@JsonProperty("movie-time") @JsonbProperty("movie-time") String movieTime,
                          @JsonProperty("client-id") @JsonbProperty("client-id") UUID clientId,
                          @JsonProperty("movie-id") @JsonbProperty("movie-id") UUID movieId) {
        this.movieTime = movieTime;
        this.clientId = clientId;
        this.movieId = movieId;
    }
}
