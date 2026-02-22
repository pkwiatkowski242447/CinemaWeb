package pl.pas.gr3.cinema.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.pas.gr3.cinema.dto.account.AccountResponse;
import pl.pas.gr3.cinema.dto.account.UpdateAccountRequest;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/admins")
@Tag(name = "1. AdminController", description = "Manage admin accounts")
public interface AdminController {

    /* READ */

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> findById(@PathVariable("id") UUID adminId);

    @GetMapping(path = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> findByLogin(@PathVariable("login") String adminLogin);

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> findSelfByLogin();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AccountResponse>> findAllWithMatchingLogin(@RequestParam("match") String adminLogin);

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AccountResponse>> findAll();

    /* UPDATE */

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch,
                                @RequestBody @Validated UpdateAccountRequest request);

    /* OTHER */

    @PostMapping(value = "/{id}/activate")
    ResponseEntity<Void> activate(@PathVariable("id") UUID adminId);

    @PostMapping(value = "/{id}/deactivate")
    ResponseEntity<Void> deactivate(@PathVariable("id") UUID adminId);
}
