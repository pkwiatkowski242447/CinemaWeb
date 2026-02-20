package pl.pas.gr3.cinema.service.api;

import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface MovieService {

    /* CREATE */

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);

    /* READ */

    Movie findByUUID(UUID movieId);
    List<Movie> findAll();

    /* UPDATE */

    void update(Movie movie);

    /* DELETE */

    void delete(UUID movieID);

    /* OTHER */

    List<Ticket> getListOfTicketsForCertainMovie(UUID movieID);
}
