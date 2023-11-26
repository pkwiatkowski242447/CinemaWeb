package pl.pas.gr3.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private UUID ticketID;
    private LocalDateTime movieTime;
    private double ticketFinalPrice;
    private UUID clientID;
    private UUID movieID;
}
