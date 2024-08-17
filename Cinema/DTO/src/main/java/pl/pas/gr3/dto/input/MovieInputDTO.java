package pl.pas.gr3.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MovieInputDTO {

    @JsonProperty("movie-title")
    private String movieTitle;

    @JsonProperty("movie-base-price")
    private double movieBasePrice;

    @JsonProperty("scr-room-number")
    private int scrRoomNumber;

    @JsonProperty("number-of-available-seats")
    private int numberOfAvailableSeats;

    @JsonCreator
    public MovieInputDTO(@JsonProperty("movie-title") String movieTitle,
                         @JsonProperty("movie-base-price") double movieBasePrice,
                         @JsonProperty("scr-room-number") int scrRoomNumber,
                         @JsonProperty("number-of-available-seats") int numberOfAvailableSeats) {
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
