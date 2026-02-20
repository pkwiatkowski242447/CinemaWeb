package pl.pas.gr3.cinema.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.pas.gr3.cinema.util.consts.model.MovieConstants;
import pl.pas.gr3.cinema.messages.validation.MovieValidationMessages;

import java.util.UUID;

@Getter @Setter
public class Movie {

    @BsonProperty(MovieConstants.GENERAL_IDENTIFIER)
    @NotNull(message = MovieValidationMessages.NULL_IDENTIFIER)
    private final UUID id;

    @BsonProperty(MovieConstants.MOVIE_TITLE)
    @NotNull(message = MovieValidationMessages.NULL_MOVIE_TITLE)
    @Size(min = MovieConstants.MOVIE_TITLE_MIN_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_SHORT)
    @Size(max = MovieConstants.MOVIE_TITLE_MAX_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_LONG)
    private String title;

    @BsonProperty(MovieConstants.MOVIE_BASE_PRICE)
    @Min(value = MovieConstants.MOVIE_BASE_PRICE_MIN_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_LOW)
    @Max(value = MovieConstants.MOVIE_BASE_PRICE_MAX_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_HIGH)
    private double basePrice;

    @BsonProperty(MovieConstants.SCREENING_ROOM_NUMBER)
    @Min(value = MovieConstants.SCREENING_ROOM_NUMBER_MIN_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_LOW)
    @Max(value = MovieConstants.SCREENING_ROOM_NUMBER_MAX_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_HIGH)
    private int scrRoomNumber;

    @BsonProperty(MovieConstants.NUMBER_OF_AVAILABLE_SEATS)
    @Min(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MIN_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_NEGATIVE)
    @Max(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MAX_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_ABOVE_LIMIT)
    private int availableSeats;

    /* Constructors */

    @BsonCreator
    public Movie(@BsonProperty(MovieConstants.GENERAL_IDENTIFIER) UUID id,
                 @BsonProperty(MovieConstants.MOVIE_TITLE) String title,
                 @BsonProperty(MovieConstants.MOVIE_BASE_PRICE) double basePrice,
                 @BsonProperty(MovieConstants.SCREENING_ROOM_NUMBER) int scrRoomNumber,
                 @BsonProperty(MovieConstants.NUMBER_OF_AVAILABLE_SEATS) int availableSeats) {
        this.id = id;
        this.title = title;
        this.basePrice = basePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.availableSeats = availableSeats;
    }

    /* Other methods */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return new EqualsBuilder()
                .append(id, movie.id)
                .append(title, movie.title)
                .append(basePrice, movie.basePrice)
                .append(scrRoomNumber, movie.scrRoomNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(title)
                .append(basePrice)
                .append(scrRoomNumber)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Movie ID: ", id)
                .append("Movie title: ", title)
                .append("Movie base price: ", basePrice)
                .append("Screening room number: ", scrRoomNumber)
                .append("Number of available seats: ", availableSeats)
                .toString();
    }
}