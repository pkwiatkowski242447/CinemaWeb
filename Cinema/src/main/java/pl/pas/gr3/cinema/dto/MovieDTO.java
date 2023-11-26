package pl.pas.gr3.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private UUID movieID;
    private String movieTitle;
    private double movieBasePrice;
    private int scrRoomNumber;
    private int numberOfAvailableSeats;
}
