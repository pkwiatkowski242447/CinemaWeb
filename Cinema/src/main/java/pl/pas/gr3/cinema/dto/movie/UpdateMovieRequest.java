package pl.pas.gr3.cinema.dto.movie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequest extends MovieSignatureDto {

    private String title;
    private double basePrice;
    private int scrRoomNumber;
    private int availableSeats;
}
