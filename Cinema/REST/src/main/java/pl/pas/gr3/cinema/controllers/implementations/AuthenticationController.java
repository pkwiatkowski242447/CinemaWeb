package pl.pas.gr3.cinema.controllers.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.services.implementations.AuthenticationService;
import pl.pas.gr3.dto.auth.UserInputDTO;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/client/register")
    public ResponseEntity<?> registerClient(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(authenticationService.registerClient(userInputDTO.getUserLogin(), userInputDTO.getUserLogin()));
    }

    @PostMapping("/client/login")
    public ResponseEntity<?> loginClient(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(authenticationService.loginClient(userInputDTO.getUserLogin(), userInputDTO.getUserLogin()));
    }

    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(authenticationService.registerAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserLogin()));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(authenticationService.loginAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserLogin()));
    }

    @PostMapping("/staff/register")
    public ResponseEntity<?> registerStaff(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(authenticationService.loginAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserLogin()));
    }

    @PostMapping("/staff/login")
    public ResponseEntity<?> loginStaff(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(authenticationService.loginStaff(userInputDTO.getUserLogin(), userInputDTO.getUserLogin()));
    }
}
