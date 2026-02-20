package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.security.services.JWSService;
import pl.pas.gr3.cinema.dto.output.MovieResponse;
import pl.pas.gr3.cinema.dto.input.CreateMovieRequest;
import pl.pas.gr3.cinema.dto.output.TicketResponse;
import pl.pas.gr3.cinema.service.impl.MovieServiceImpl;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.controller.api.MovieIController;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MovieControllerImpl implements MovieIController {

    private final MovieServiceImpl movieService;
    private final JWSService jwsService;

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<MovieResponse> create(CreateMovieRequest createMovieRequest) {
        Movie movie = movieService.create(
            createMovieRequest.getTitle(), createMovieRequest.getBasePrice(), createMovieRequest.getScrRoomNumber(),
            createMovieRequest.getAvailableSeats()
        );

        MovieResponse movieResponse = new MovieResponse(movie.getId(), movie.getTitle(), movie.getBasePrice(), movie.getScrRoomNumber(), movie.getAvailableSeats());

        String location = MessageFormat.format("http://localhost:8000/api/v1/movies/{0}", movieResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(movieResponse);
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('CLIENT')")
    @Override
    public ResponseEntity<List<MovieResponse>> findAll() {
        List<Movie> foundMovies = movieService.findAll();
        List<MovieResponse> outputDtos = foundMovies.stream().map(movie ->
            new MovieResponse(movie.getId(), movie.getTitle(), movie.getBasePrice(), movie.getScrRoomNumber(), movie.getAvailableSeats())
        ).toList();

        return outputDtos.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(outputDtos);
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('CLIENT')")
    @Override
    public ResponseEntity<MovieResponse> findById(UUID movieId) {
        Movie movie = movieService.findByUUID(movieId);
        MovieResponse movieResponse = new MovieResponse(movie.getId(), movie.getTitle(), movie.getBasePrice(), movie.getScrRoomNumber(), movie.getAvailableSeats());

        String signature = jwsService.generateSignatureForMovie(movie);
        return ResponseEntity.ok().eTag(signature).body(movieResponse);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketResponse>> findAllTicketsForMovie(UUID movieId) {
        List<Ticket> foundTickets = movieService.getListOfTicketsForCertainMovie(movieId);
        List<TicketResponse> outputDtos = foundTickets.stream().map(ticket ->
            new TicketResponse(ticket.getId(), ticket.getMovieTime(), ticket.getPrice(), ticket.getUserId(), ticket.getMovieId())
        ).toList();

        return outputDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(outputDtos);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, MovieResponse movieResponse) {
        Movie movie = new Movie(movieResponse.getId(), movieResponse.getTitle(), movieResponse.getBasePrice(), movieResponse.getScrRoomNumber(), movieResponse.getAvailableSeats());

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
