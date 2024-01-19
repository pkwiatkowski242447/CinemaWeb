package pl.pas.gr3.cinema.mapping.docs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class MovieDoc {

    @BsonProperty("_id")
    private UUID movieID;

    @BsonProperty("movie_title")
    private String movieTitle;

    @BsonProperty("movie_base_price")
    private double movieBasePrice;

    @BsonProperty("scr_room_number")
    private int scrRoomNumber;

    @BsonProperty("number_of_available_seats")
    private int numberOfAvailableSeats;

    @BsonCreator
    public MovieDoc(@BsonProperty("_id") UUID movieID,
                    @BsonProperty("movie_title") String movieTitle,
                    @BsonProperty("movie_base_price") double movieBasePrice,
                    @BsonProperty("scr_room_number") int scrRoomNumber,
                    @BsonProperty("number_of_available_seats") int numberOfAvailableSeats) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.movieBasePrice = movieBasePrice;
        this.scrRoomNumber = scrRoomNumber;
        this.numberOfAvailableSeats = numberOfAvailableSeats;
    }
}
