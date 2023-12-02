package pl.pas.gr3.cinema.managers.interfaces;

import pl.pas.gr3.cinema.exceptions.managers.GeneralMovieManagerException;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface MovieManagerInterface extends ManagerInterface<Movie> {

    // Create methods

    Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws GeneralMovieManagerException;

    // Update methods

    void update(Movie movie) throws GeneralMovieManagerException;

    // Delete methods

    void delete(UUID movieID) throws GeneralMovieManagerException;

    // Other methods

    List<Ticket> getListOfTicketsForCertainMovie(UUID movieID);
}
