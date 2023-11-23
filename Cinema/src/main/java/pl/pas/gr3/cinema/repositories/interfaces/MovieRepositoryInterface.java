package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface MovieRepositoryInterface extends RepositoryInterface<Movie> {

    // Create methods

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);

    // Other methods

    List<Ticket> getListOfTicketsForMovie(UUID movieID);
}
