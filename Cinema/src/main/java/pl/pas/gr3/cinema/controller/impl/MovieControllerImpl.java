package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.dto.output.MovieDTO;
import pl.pas.gr3.cinema.dto.input.MovieInputDTO;
import pl.pas.gr3.cinema.dto.output.TicketDTO;
import pl.pas.gr3.cinema.service.impl.MovieServiceImpl;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.controller.api.MovieIController;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MovieControllerImpl implements MovieIController {

    private final MovieServiceImpl movieService;
    private final JWSService jwsService;

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<MovieDTO> create(MovieInputDTO movieInputDto) {
        Movie movie = movieService.create(
            movieInputDto.getTitle(), movieInputDto.getBasePrice(), movieInputDto.getScrRoomNumber(),
            movieInputDto.getAvailableSeats()
        );

        MovieDTO movieDto = new MovieDTO(movie.getId(), movie.getTitle(), movie.getBasePrice(), movie.getScrRoomNumber(), movie.getAvailableSeats());

        String location = MessageFormat.format("http://localhost:8000/api/v1/movies/{0}", movieDto.getId());
        return ResponseEntity.created(URI.create(location)).body(movieDto);
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('CLIENT')")
    @Override
    public ResponseEntity<List<MovieDTO>> findAll() {
        List<Movie> foundMovies = movieService.findAll();
        List<MovieDTO> outputDtos = foundMovies.stream().map(movie ->
            new MovieDTO(movie.getId(), movie.getTitle(), movie.getBasePrice(), movie.getScrRoomNumber(), movie.getAvailableSeats())
        ).toList();

        return outputDtos.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(outputDtos);
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('CLIENT')")
    @Override
    public ResponseEntity<MovieDTO> findById(UUID movieId) {
        Movie movie = movieService.findByUUID(movieId);
        MovieDTO movieDto = new MovieDTO(movie.getId(), movie.getTitle(), movie.getBasePrice(), movie.getScrRoomNumber(), movie.getAvailableSeats());

        String signature = jwsService.generateSignatureForMovie(movie);
        return ResponseEntity.ok().eTag(signature).body(movieDto);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketDTO>> findAllTicketsForMovie(UUID movieId) {
        List<Ticket> foundTickets = movieService.getListOfTicketsForCertainMovie(movieId);
        List<TicketDTO> outputDtos = foundTickets.stream().map(ticket ->
            new TicketDTO(ticket.getId(), ticket.getMovieTime(), ticket.getPrice(), ticket.getUserId(), ticket.getMovieId())
        ).toList();

        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, MovieDTO movieDto) {
        Movie movie = new Movie(movieDto.getId(), movieDto.getTitle(), movieDto.getBasePrice(), movieDto.getScrRoomNumber(), movieDto.getAvailableSeats());

        /* TODO: Fix here */
        String ifMatchContent = ifMatch.replace("\"", "");
        if (!jwsService.verifyMovieSignature(ifMatchContent, movie))
            throw new ApplicationDataIntegrityCompromisedException();

        movieService.update(movie);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<Void> delete(UUID movieId) {
        movieService.delete(movieId);
        return ResponseEntity.noContent().build();
    }
}
