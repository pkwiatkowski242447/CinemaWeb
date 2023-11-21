package pl.pas.gr3.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Movie {

    private final UUID movieID;

    private String movieTitle;

    private double movieBasePrice;

    private int scrRoomNumber;

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
                .append(numberOfAvailableSeats, movie.numberOfAvailableSeats)
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
                .append(numberOfAvailableSeats)
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
