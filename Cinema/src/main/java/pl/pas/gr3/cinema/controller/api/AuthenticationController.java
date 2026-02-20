package pl.pas.gr3.cinema.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.pas.gr3.cinema.dto.LoginResponse;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.LoginAccountRequest;

@RequestMapping(path = "/api/v1/auth")
@Tag(name = "4. AuthenticationController", description = "Manage account registration & authentication")
public interface AuthenticationController {

    /* REGISTER */

    @PostMapping(path = "/register/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> registerClient(@RequestBody @Validated LoginAccountRequest userInputDTO);

    @PostMapping(path = "/register/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> registerAdmin(@RequestBody @Validated LoginAccountRequest userInputDTO);

    @PostMapping(path = "/register/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> registerStaff(@RequestBody @Validated LoginAccountRequest userInputDTO);

    /* LOGIN */

    @PostMapping(value = "/login/client")
    ResponseEntity<LoginResponse> loginClient(@RequestBody @Validated LoginAccountRequest userInputDTO);

    @PostMapping("/login/admin")
    ResponseEntity<LoginResponse> loginAdmin(@RequestBody @Validated LoginAccountRequest userInputDTO);

    @PostMapping("/login/staff")
    ResponseEntity<LoginResponse> loginStaff(@RequestBody @Validated LoginAccountRequest userInputDTO);

    /* OTHER */

    @PostMapping(path = "/logout")
    ResponseEntity<Void> logout();
}
