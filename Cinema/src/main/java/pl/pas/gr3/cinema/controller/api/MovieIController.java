package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pas.gr3.cinema.dto.input.MovieInputDTO;
import pl.pas.gr3.cinema.dto.output.MovieDTO;
import pl.pas.gr3.cinema.dto.output.TicketDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/movies")
public interface MovieIController {

    /* CREATE */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MovieDTO> create(@RequestBody MovieInputDTO movieInputDto);

    /* READ */

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MovieDTO> findById(@PathVariable("id") UUID movieId);

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<MovieDTO>> findAll();

    @GetMapping(path = "/{id}/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TicketDTO>> findAllTicketsForMovie(@PathVariable("id") UUID movieId);

    /* UPDATE */

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
                             @RequestBody MovieDTO movieDto);

    /* DELETE */

    @DeleteMapping(path = "/{id}/delete")
    ResponseEntity<Void> delete(@PathVariable("id") UUID movieId);
}
