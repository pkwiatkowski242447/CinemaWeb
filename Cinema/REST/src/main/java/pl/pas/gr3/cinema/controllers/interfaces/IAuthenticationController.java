package pl.pas.gr3.cinema.controllers.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.pas.gr3.cinema.common.dto.authentication.AccountCredentialLoginDTO;

@RequestMapping(path = "/api/v1/auth")
public interface IAuthenticationController {

    @PostMapping(path = "/login-credentials", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> loginWithCredentials(@RequestBody AccountCredentialLoginDTO loginDTO);

    @PostMapping(path = "/logout")
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
}
