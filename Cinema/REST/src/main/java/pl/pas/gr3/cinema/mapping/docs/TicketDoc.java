package pl.pas.gr3.cinema.mapping.docs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class TicketDoc {

    @BsonProperty("_id")
    private UUID ticketID;

    @BsonProperty("movie_time")
    private LocalDateTime movieTime;

    @BsonProperty("movie_final_price")
    private double ticketFinalPrice;

    @BsonProperty("client_id")
    private UUID clientID;

    @BsonProperty("movie_id")
    private UUID movieID;

    @BsonCreator
    public TicketDoc(@BsonProperty("_id") UUID ticketID,
                     @BsonProperty("movie_time") LocalDateTime movieTime,
                     @BsonProperty("movie_final_price") double ticketFinalPrice,
                     @BsonProperty("client_id") UUID clientID,
                     @BsonProperty("movie_id") UUID movieID) {
        this.ticketID = ticketID;
        this.movieTime = movieTime;
        this.ticketFinalPrice = ticketFinalPrice;
        this.clientID = clientID;
        this.movieID = movieID;
    }
}
