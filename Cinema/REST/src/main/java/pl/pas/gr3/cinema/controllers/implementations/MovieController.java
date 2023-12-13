package pl.pas.gr3.cinema.controllers.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pas.gr3.cinema.exceptions.services.crud.movie.MovieServiceMovieNotFoundException;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.MovieInputDTO;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.cinema.exceptions.services.GeneralServiceException;
import pl.pas.gr3.cinema.services.implementations.MovieService;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.controllers.interfaces.MovieServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController implements MovieServiceInterface {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> create(@RequestBody MovieInputDTO movieInputDTO) {
        try {
            Movie movie = this.movieService.create(movieInputDTO.getMovieTitle(), movieInputDTO.getMovieBasePrice(), movieInputDTO.getScrRoomNumber(), movieInputDTO.getNumberOfAvailableSeats());

            Set<ConstraintViolation<Movie>> violationSet = validator.validate(movie);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            MovieDTO movieDTO = new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/movies/" + movieDTO.getMovieID().toString())).contentType(MediaType.APPLICATION_JSON).body(movieDTO);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<Movie> listOfFoundMovies = this.movieService.findAll();
            List<MovieDTO> listOfDTOs = new ArrayList<>();
            for (Movie movie : listOfFoundMovies) {
                listOfDTOs.add(new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats()));
            }

            if (listOfDTOs.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID movieID) {
        try {
            Movie movie = this.movieService.findByUUID(movieID);
            MovieDTO movieDTO = new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(movieDTO);
        } catch (MovieServiceMovieNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "{id}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllTicketsForCertainMovie(@PathVariable("id") UUID movieID) {
        List<Ticket> listOfTickets = this.movieService.getListOfTicketsForCertainMovie(movieID);
        List<TicketDTO> listOfDTOs = new ArrayList<>();
        for (Ticket ticket : listOfTickets) {
            listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
        }

        if (listOfDTOs.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> update(@RequestBody MovieDTO movieDTO) {
        try {
            Movie movie = new Movie(movieDTO.getMovieID(), movieDTO.getMovieTitle(), movieDTO.getMovieBasePrice(), movieDTO.getScrRoomNumber(), movieDTO.getNumberOfAvailableSeats());

            Set<ConstraintViolation<Movie>> violationSet = validator.validate(movie);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            this.movieService.update(movie);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") UUID movieID) {
        try {
            this.movieService.delete(movieID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }
}
