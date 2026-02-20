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
import pl.pas.gr3.cinema.dto.input.CreateTicketRequest;
import pl.pas.gr3.cinema.dto.input.CreateOwnTicketRequest;
import pl.pas.gr3.cinema.dto.output.TicketResponse;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/tickets")
@Tag(name = "5. TicketController", description = "Manage tickets")
public interface TicketController {

    /* CREATE */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TicketResponse> create(@RequestBody @Validated CreateTicketRequest createTicketRequest);

    @PostMapping(value = "/self", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TicketResponse> create(@RequestBody @Validated CreateOwnTicketRequest createOwnTicketRequest);

    /* READ */

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TicketResponse> findById(@PathVariable("id") UUID ticketId);

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TicketResponse>> findAll();

    /* UPDATE */

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
                                @RequestBody @Validated TicketResponse ticketResponse);

    /* DELETE */

    @DeleteMapping(value = "/{id}/delete")
    ResponseEntity<Void> delete(@PathVariable("id") UUID ticketId);
}
