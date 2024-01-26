package pl.pas.gr3.dto.input;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TicketSelfInputDTO {

    private String movieTime;
    private UUID movieID;
}
