package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.ResponseEntity;
import pl.pas.gr3.cinema.dto.input.MovieInputDTO;
import pl.pas.gr3.cinema.model.Movie;

import java.util.UUID;

public interface IMovieIService extends IService<Movie> {

    // Create methods

    ResponseEntity<?> create(MovieInputDTO movieInputDTO);

    // Update methods

    ResponseEntity<?> delete(UUID movieID);

    // Other methods

    ResponseEntity<?> findAllTicketsForCertainMovie(UUID movieID);
}
