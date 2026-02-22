package pl.pas.gr3.cinema.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.dto.movie.UpdateMovieRequest;
import pl.pas.gr3.cinema.exception.pre_condition.ApplicationDataIntegrityCompromisedException;
import pl.pas.gr3.cinema.mapper.MovieMapper;
import pl.pas.gr3.cinema.mapper.TicketMapper;
import pl.pas.gr3.cinema.service.api.MovieService;
import pl.pas.gr3.cinema.service.impl.JWSService;
import pl.pas.gr3.cinema.dto.movie.MovieResponse;
import pl.pas.gr3.cinema.dto.movie.CreateMovieRequest;
import pl.pas.gr3.cinema.dto.ticket.TicketResponse;
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

    private final MovieService movieService;
    private final JWSService jwsService;

    private final MovieMapper movieMapper;
    private final TicketMapper ticketMapper;

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<MovieResponse> create(CreateMovieRequest request) {
        Movie movie = movieService.create(
            request.title(), request.basePrice(), request.scrRoomNumber(),
            request.availableSeats()
        );

        MovieResponse movieResponse = movieMapper.toResponse(movie);

        String location = MessageFormat.format("http://localhost:8000/api/v1/movies/{0}", movieResponse.getId());
        return ResponseEntity.created(URI.create(location)).body(movieResponse);
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('CLIENT')")
    @Override
    public ResponseEntity<List<MovieResponse>> findAll() {
        List<Movie> foundMovies = movieService.findAll();
        List<MovieResponse> movieResponses = foundMovies.stream().map(movieMapper::toResponse).toList();
        return movieResponses.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(movieResponses);
    }

    @PreAuthorize("hasRole('STAFF') or hasRole('CLIENT')")
    @Override
    public ResponseEntity<MovieResponse> findById(UUID movieId) {
        Movie movie = movieService.findByUUID(movieId);
        MovieResponse movieResponse = movieMapper.toResponse(movie);
        String signature = jwsService.generateSignature(movieResponse);
        return ResponseEntity.ok().eTag(signature).body(movieResponse);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<List<TicketResponse>> findAllTicketsForMovie(UUID movieId) {
        List<Ticket> foundTickets = movieService.getListOfTicketsForCertainMovie(movieId);
        List<TicketResponse> ticketResponses = foundTickets.stream().map(ticketMapper::toResponse).toList();
        return ticketResponses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ticketResponses);
    }

    @PreAuthorize("hasRole('STAFF')")
    @Override
    public ResponseEntity<Void> update(String ifMatch, UpdateMovieRequest request) {
        String signature = jwsService.generateSignature(request);
        if (!signature.equals(ifMatch)) throw new ApplicationDataIntegrityCompromisedException();

        Movie movie = new Movie(
            request.getId(), request.getTitle(), request.getBasePrice(),
            request.getScrRoomNumber(), request.getAvailableSeats()
        );

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
