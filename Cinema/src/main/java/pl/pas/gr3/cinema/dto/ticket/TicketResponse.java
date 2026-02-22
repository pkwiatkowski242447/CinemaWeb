package pl.pas.gr3.cinema.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse extends TicketSignatureDto {

    private LocalDateTime movieTime;
    private double finalPrice;
    private UUID clientId;
    private UUID movieId;
}
