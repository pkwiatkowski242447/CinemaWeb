package pl.pas.gr3.cinema.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.UpdateAccountRequest;
import pl.pas.gr3.cinema.dto.output.TicketDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/clients")
public interface ClientController {

    /* READ */

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AccountResponse>> findAll();

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> findById(@PathVariable("id") UUID clientId);

    @GetMapping(value = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> findByLogin(@PathVariable("login") String clientLogin);

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> findSelfByLogin();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(@RequestParam("match") String clientLogin);

    @GetMapping(value = "/{id}/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TicketDTO>> getTicketsForCertainUser(@PathVariable("id") UUID clientId);

    @GetMapping(value = "/self/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TicketDTO>> getTicketsForCertainUser();

    /* UPDATE */

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
                             @RequestBody @Valid UpdateAccountRequest userUpdateDto);

    /* OTHER */

    @PostMapping(value = "/{id}/activate")
    ResponseEntity<Void> activate(@PathVariable("id") UUID clientId);

    @PostMapping(value = "/{id}/deactivate")
    ResponseEntity<Void> deactivate(@PathVariable("id") UUID clientId);
}
