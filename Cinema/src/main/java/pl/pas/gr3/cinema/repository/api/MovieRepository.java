package pl.pas.gr3.cinema.repository.api;

import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface MovieRepository extends Repository<Movie> {

    /* CREATE */

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);

    /* READ*/

    List<Movie> findAll();

    /* UPDATE */

    void update(Movie movie);

    /* DELETE */

    void delete(UUID movieId);

    /* OTHER */

    List<Ticket> getListOfTicketsForMovie(UUID movieId);
}
