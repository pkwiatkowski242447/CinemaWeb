package pl.pas.gr3.cinema.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.pas.gr3.cinema.dto.movie.CreateMovieRequest;
import pl.pas.gr3.cinema.dto.movie.MovieResponse;
import pl.pas.gr3.cinema.dto.movie.UpdateMovieRequest;
import pl.pas.gr3.cinema.dto.ticket.TicketResponse;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/movies")
@Tag(name = "5. MovieController", description = "Manage movies")
public interface MovieIController {

    /* CREATE */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MovieResponse> create(@RequestBody CreateMovieRequest request);

    /* READ */

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MovieResponse> findById(@PathVariable("id") UUID movieId);

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<MovieResponse>> findAll();

    @GetMapping(path = "/{id}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TicketResponse>> findAllTicketsForMovie(@PathVariable("id") UUID movieId);

    /* UPDATE */

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
                                @RequestBody @Validated UpdateMovieRequest request);

    /* DELETE */

    @DeleteMapping(path = "/{id}/delete")
    ResponseEntity<Void> delete(@PathVariable("id") UUID movieId);
}
