package pl.pas.gr3.cinema.services.interfaces;

import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.model.Movie;

import java.util.UUID;

public interface MovieServiceInterface extends ServiceInterface<Movie> {

    // Create methods

    Response create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats);

    // Update methods

    Response delete(UUID movieID);

    // Other methods

    Response findAllTicketsForCertainMovie(UUID movieID);

    // Update methods
    Response update(Movie movie);
}
