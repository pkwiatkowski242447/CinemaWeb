package pl.pas.gr3.cinema.repository.dto;

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
public class MovieResponse {

    @JsonProperty("movie-id")
    @JsonbProperty("movie-id")
    private UUID id;

    @JsonProperty("movie-title")
    @JsonbProperty("movie-title")
    private String title;

    @JsonProperty("movie-base-price")
    @JsonbProperty("movie-base-price")
    private double basePrice;

    @JsonProperty("scr-room-number")
    @JsonbProperty("scr-room-number")
    private int scrRoomNumber;

    @JsonProperty("number-of-available-seats")
    @JsonbProperty("number-of-available-seats")
    private int availableSeats;

    @JsonCreator
    @JsonbCreator
    public MovieResponse(@JsonProperty("movie-id") @JsonbProperty("movie-id") UUID id,
                         @JsonProperty("movie-title") @JsonbProperty("movie-title") String title,
                         @JsonProperty("movie-base-price") @JsonbProperty("movie-base-price") double basePrice,
                         @JsonProperty("scr-room-number") @JsonbProperty("scr-room-number") int scrRoomNumber,
                         @JsonProperty("number-of-available-seats") @JsonbProperty("number-of-available-seats") int availableSeats) {
        this.id = id;
        this.title = title;
        this.basePrice = basePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.availableSeats = availableSeats;
    }
}
