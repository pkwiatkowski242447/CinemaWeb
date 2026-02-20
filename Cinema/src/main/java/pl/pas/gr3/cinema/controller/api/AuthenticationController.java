package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.dto.LoginResponse;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.LoginAccountRequest;

@RestController
@RequestMapping(path = "/api/v1/auth")
public interface AuthenticationController {

    /* REGISTER */

    @PostMapping(path = "/register/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> registerClient(@RequestBody LoginAccountRequest userInputDTO);

    @PostMapping(path = "/register/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> registerAdmin(@RequestBody LoginAccountRequest userInputDTO);

    @PostMapping(path = "/register/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> registerStaff(@RequestBody LoginAccountRequest userInputDTO);

    /* LOGIN */

    @PostMapping(value = "/login/client")
    ResponseEntity<LoginResponse> loginClient(@RequestBody LoginAccountRequest userInputDTO);

    @PostMapping("/login/admin")
    ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginAccountRequest userInputDTO);

    @PostMapping("/login/staff")
    ResponseEntity<LoginResponse> loginStaff(@RequestBody LoginAccountRequest userInputDTO);

    /* OTHER */

    @PostMapping(path = "/logout")
    ResponseEntity<Void> logout();
}
