package pl.pas.gr3.cinema.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieInputDTO {

    @JsonbProperty("movie-title")
    private String movieTitle;

    @JsonbProperty("movie-base-price")
    private double movieBasePrice;

    @JsonbProperty("scr-room-number")
    private int scrRoomNumber;

    @JsonbProperty("number-of-available-seats")
    private int numberOfAvailableSeats;

    @JsonbCreator
    public MovieInputDTO(@JsonbProperty("movie-title") String movieTitle,
                         @JsonbProperty("movie-base-price") double movieBasePrice,
                         @JsonbProperty("scr-room-number") int scrRoomNumber,
                         @JsonbProperty("number-of-available-seats") int numberOfAvailableSeats) {
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
