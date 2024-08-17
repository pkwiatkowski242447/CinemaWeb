package pl.pas.gr3.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class MovieDTO {

    @JsonProperty("movie-id")
    private UUID movieID;

    @JsonProperty("movie-title")
    private String movieTitle;

    @JsonProperty("movie-base-price")
    private double movieBasePrice;

    @JsonProperty("scr-room-number")
    private int scrRoomNumber;

    @JsonProperty("number-of-available-seats")
    private int numberOfAvailableSeats;

    @JsonCreator
    public MovieDTO(@JsonProperty("movie-id")  UUID movieID,
                    @JsonProperty("movie-title") String movieTitle,
                    @JsonProperty("movie-base-price") double movieBasePrice,
                    @JsonProperty("scr-room-number") int scrRoomNumber,
                    @JsonProperty("number-of-available-seats") int numberOfAvailableSeats) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
