package pl.pas.gr3.mvc.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.mvc.constants.MovieConstants;
import pl.pas.gr3.mvc.messages.MovieValidationMessages;

@Getter @Setter
@NoArgsConstructor
public class Movie {

    @NotNull(message = MovieValidationMessages.NULL_MOVIE_TITLE)
    @Size(min = MovieConstants.MOVIE_TITLE_MIN_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_SHORT)
    @Size(max = MovieConstants.MOVIE_TITLE_MAX_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_LONG)
    private String movieTitle;

    @Min(value = MovieConstants.MOVIE_BASE_PRICE_MIN_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_LOW)
    @Max(value = MovieConstants.MOVIE_BASE_PRICE_MAX_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_HIGH)
    private double movieBasePrice;

    @Min(value = MovieConstants.SCREENING_ROOM_NUMBER_MIN_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_LOW)
    @Max(value = MovieConstants.SCREENING_ROOM_NUMBER_MAX_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_HIGH)
    private int scrRoomNumber;

    @Min(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MIN_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_NEGATIVE)
    @Max(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MAX_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_ABOVE_LIMIT)
    private int numberOfAvailableSeats;

    // Constructors

    public Movie(@NotNull(message = MovieValidationMessages.NULL_MOVIE_TITLE)
                 @Size(min = MovieConstants.MOVIE_TITLE_MIN_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_SHORT)
                 @Size(max = MovieConstants.MOVIE_TITLE_MAX_LENGTH, message = MovieValidationMessages.MOVIE_TITLE_TOO_LONG) String movieTitle,
                 @Min(value = MovieConstants.MOVIE_BASE_PRICE_MIN_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_LOW)
                 @Max(value = MovieConstants.MOVIE_BASE_PRICE_MAX_VALUE, message = MovieValidationMessages.MOVIE_BASE_PRICE_TOO_HIGH) double movieBasePrice,
                 @Min(value = MovieConstants.SCREENING_ROOM_NUMBER_MIN_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_LOW)
                 @Max(value = MovieConstants.SCREENING_ROOM_NUMBER_MAX_VALUE, message = MovieValidationMessages.SCREENING_ROOM_NUMBER_TOO_HIGH) int scrRoomNumber,
                 @Min(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MIN_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_NEGATIVE)
                 @Max(value = MovieConstants.NUMBER_OF_AVAILABLE_SEATS_MAX_VALUE, message = MovieValidationMessages.NUMBER_OF_AVAILABLE_SEATS_ABOVE_LIMIT) int numberOfAvailableSeats) {
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
