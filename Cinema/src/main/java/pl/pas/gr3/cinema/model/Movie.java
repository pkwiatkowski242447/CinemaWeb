package pl.pas.gr3.cinema.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.pas.gr3.cinema.validation.MovieValidationMessages;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Movie {

    @NotNull(message = MovieValidationMessages.NULL_IDENTIFIER)
    private final UUID movieID;

    @NotNull(message = MovieValidationMessages.NULL_MOVIE_TITLE)
    @Size(min = 1, message = MovieValidationMessages.MOVIE_TITLE_TOO_SHORT)
    @Size(max = 150, message = MovieValidationMessages.MOVIE_TITLE_TOO_LONG)
    private String movieTitle;

    @Min(value = 0, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_LOW)
    @Max(value = 100, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_HIGH)
    private double movieBasePrice;

    @Min(value = 1, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_LOW)
    @Max(value = 30, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_HIGH)
    private int scrRoomNumber;

    @Min(value = 0, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_NEGATIVE)
    @Max(value = 120, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_ABOVE_LIMIT)
    private int numberOfAvailableSeats;

    // Constructors

    // Equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return new EqualsBuilder()
                .append(movieID, movie.movieID)
                .append(movieTitle, movie.movieTitle)
                .append(movieBasePrice, movie.movieBasePrice)
                .append(scrRoomNumber, movie.scrRoomNumber)
                .isEquals();
    }

    // HashCode method

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(movieID)
                .append(movieTitle)
                .append(movieBasePrice)
                .append(scrRoomNumber)
                .toHashCode();
    }


    // ToString method

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Movie ID: ", movieID)
                .append("Movie title: ", movieTitle)
                .append("Movie base price: ", movieBasePrice)
                .append("Screening room number: ", scrRoomNumber)
                .append("Number of available seats: ", numberOfAvailableSeats)
                .toString();
    }
}
