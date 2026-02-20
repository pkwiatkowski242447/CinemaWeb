package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.dto.input.TicketInputDTO;
import pl.pas.gr3.cinema.dto.input.TicketSelfInputDTO;
import pl.pas.gr3.cinema.dto.output.TicketDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tickets")
public interface TicketController {

    /* CREATE */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TicketDTO> create(@RequestBody TicketInputDTO ticketInputDto);

    @PostMapping(value = "/self", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TicketDTO> create(@RequestBody TicketSelfInputDTO ticketSelfInputDto);

    /* READ */

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TicketDTO> findById(@PathVariable("id") UUID ticketId);

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TicketDTO>> findAll();

    /* UPDATE */

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody TicketDTO ticketDto);

    /* DELETE */

    @DeleteMapping(value = "/{id}/delete")
    ResponseEntity<Void> delete(@PathVariable("id") UUID ticketId);
}
