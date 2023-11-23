package pl.pas.gr3.cinema.dto.json;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class MovieJson {

    @JsonbProperty("id")
    private UUID movieID;

    @JsonbProperty("title")
    private String movieTitle;

    @JsonbProperty("price")
    private double movieBasePrice;

    @JsonbProperty("scr-room")
    private int screeningRoomNumber;

    @JsonbProperty("number")
    private int numberOfAvailableSeats;

    @JsonbCreator
    public MovieJson(@JsonbProperty("id") UUID movieID,
                     @JsonbProperty("title") String movieTitle,
                     @JsonbProperty("price") double movieBasePrice,
                     @JsonbProperty("scr-room") int screeningRoomNumber,
                     @JsonbProperty("number") int numberOfAvailableSeats) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.screeningRoomNumber = screeningRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
