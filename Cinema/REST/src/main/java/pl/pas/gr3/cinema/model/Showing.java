package pl.pas.gr3.cinema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;
import pl.pas.gr3.cinema.utils.messages.ShowingConstants;

import java.time.LocalDateTime;

@Entity
@Table(
        name = DatabaseConstants.SHOWINGS_TABLE,
        indexes = {
                @Index(name = DatabaseConstants.SHOWINGS_MOVIE_ID_IDX,
                        columnList = DatabaseConstants.SHOWINGS_MOVIE_ID_COLUMN)
        }
)
@Getter @Setter
@NoArgsConstructor
public class Showing extends AbstractEntity {

    @NotNull(message = ShowingConstants.SHOWING_BASE_PRICE_NULL)
    @Column(name = DatabaseConstants.SHOWINGS_SHOW_TIME_COLUMN, nullable = false, updatable = false)
    private LocalDateTime showTime;

    @NotNull(message = ShowingConstants.SHOWING_BASE_PRICE_NULL)
    @Min(value = ShowingConstants.SHOWING_BASE_PRICE_MIN_VALUE, message = ShowingConstants.SHOWING_BASE_PRICE_NEGATIVE)
    @Max(value = ShowingConstants.SHOWING_BASE_PRICE_MAX_VALUE, message = ShowingConstants.SHOWING_TITLE_TOO_HIGH)
    @Column(name = DatabaseConstants.SHOWINGS_BASE_PRICE_COLUMN, nullable = false)
    private Double basePrice;

    @NotNull(message = ShowingConstants.SHOWING_ROOM_NUMBER_NULL)
    @Min(value = ShowingConstants.SHOWING_ROOM_NUMBER_MIN_VALUE, message = ShowingConstants.SHOWING_ROOM_NUMBER_NON_POSITIVE)
    @Max(value = ShowingConstants.SHOWING_ROOM_NUMBER_MAX_VALUE, message = ShowingConstants.SHOWING_ROOM_NUMBER_TOO_HIGH)
    @Column(name = DatabaseConstants.SHOWINGS_ROOM_NUMBER_COLUMN, nullable = false)
    private Integer roomNumber;

    @NotNull(message = ShowingConstants.SHOWING_AVAILABLE_SEATS_NULL)
    @Min(value = ShowingConstants.SHOWING_AVAILABLE_SEATS_MIN_VALUE, message = ShowingConstants.SHOWING_AVAILABLE_SEATS_NEGATIVE)
    @Max(value = ShowingConstants.SHOWING_AVAILABLE_SEATS_MAX_VALUE, message = ShowingConstants.SHOWING_AVAILABLE_SEATS_TOO_HIGH)
    @Column(name = DatabaseConstants.SHOWINGS_AVAILABLE_SEATS_COLUMN, nullable = false)
    private Integer availableSeats;

    @ManyToOne
    @JoinColumn(
            name = DatabaseConstants.SHOWINGS_MOVIE_ID_COLUMN,
            referencedColumnName = DatabaseConstants.PK_COLUMN,
            foreignKey = @ForeignKey(name = DatabaseConstants.SHOWINGS_MOVIE_ID_FK)
    )
    private Movie movie;
}
