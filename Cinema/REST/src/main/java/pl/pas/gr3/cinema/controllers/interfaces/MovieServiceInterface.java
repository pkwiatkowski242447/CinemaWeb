package pl.pas.gr3.cinema.controllers.interfaces;

import jakarta.ws.rs.core.Response;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.MovieInputDTO;
import pl.pas.gr3.cinema.model.Movie;

import java.util.UUID;

public interface MovieServiceInterface extends ServiceInterface<Movie> {

    // Create methods

    Response create(MovieInputDTO movieInputDTO);

    // Update methods

    Response delete(UUID movieID);

    // Other methods

    Response findAllTicketsForCertainMovie(UUID movieID);

    // Update methods
    Response update(MovieDTO movieDTO);
}
