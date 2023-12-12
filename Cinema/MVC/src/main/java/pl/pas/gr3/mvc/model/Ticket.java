package pl.pas.gr3.mvc.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Valid
@Getter @Setter @NoArgsConstructor
public class Ticket {

    @NotNull
    @NotEmpty
    private String movieTime;

    @NotNull
    private UUID clientID;

    @NotNull
    private UUID movieID;

    @NotNull
    @NotEmpty
    private String ticketType;

    public Ticket(@NotNull String movieTime, @NotNull UUID clientID, @NotNull UUID movieID, @NotNull String ticketType) {
        this.movieTime = movieTime;
        this.clientID = clientID;
        this.movieID = movieID;
        this.ticketType = ticketType;
    }
}
