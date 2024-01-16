package pl.pas.gr3.mvc.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import pl.pas.gr3.mvc.messages.TicketValidationMessages;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Ticket {

    private LocalDateTime movieTime;

    @NotNull(message = TicketValidationMessages.NULL_CLIENT_REFERENCE)
    private UUID userID;

    @NotNull(message = TicketValidationMessages.NULL_MOVIE_REFERENCE)
    private UUID movieID;

    // Constructors

    public Ticket() {

    }

    public Ticket(LocalDateTime movieTime,
                  UUID userID,
                  UUID movieID) {
        this.movieTime = movieTime;
        this.userID = userID;
        this.movieID = movieID;
    }
}
