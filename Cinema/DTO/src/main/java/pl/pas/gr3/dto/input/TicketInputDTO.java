package pl.pas.gr3.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketInputDTO {

    @JsonProperty("movie-time")
    private String movieTime;

    @JsonProperty("client-id")
    private UUID clientID;

    @JsonProperty("movie-id")
    private UUID movieID;

    @JsonCreator
    public TicketInputDTO(@JsonProperty("movie-time") String movieTime,
                          @JsonProperty("client-id") UUID clientID,
                          @JsonProperty("movie-id") UUID movieID) {
        this.movieTime = movieTime;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
