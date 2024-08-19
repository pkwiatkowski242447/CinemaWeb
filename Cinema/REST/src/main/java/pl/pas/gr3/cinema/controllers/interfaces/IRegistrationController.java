package pl.pas.gr3.cinema.controllers.interfaces;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.pas.gr3.cinema.common.dto.account.AccountRegisterDTO;

@RequestMapping(path = "/api/v1/register")
public interface IRegistrationController {

    @RequestBody(content = @Content(encoding = @Encoding(name = "accountRegisterDTO",
            contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(path = "/client", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> registerClient(@RequestPart(value = "accountRegisterDTO") AccountRegisterDTO accountRegisterDTO,
                                     @RequestPart(value = "avatar", required = false) MultipartFile avatar);

    @RequestBody(content = @Content(encoding = @Encoding(name = "accountRegisterDTO",
            contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(path = "/staff", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> registerStaff(@RequestPart(value = "accountRegisterDTO") AccountRegisterDTO accountRegisterDTO,
                                    @RequestPart(value = "avatar", required = false) MultipartFile avatar);

    @RequestBody(content = @Content(encoding = @Encoding(name = "accountRegisterDTO",
            contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(path = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> registerAdmin(@RequestPart(value = "accountRegisterDTO") AccountRegisterDTO accountRegisterDTO,
                                    @RequestPart(value = "avatar", required = false) MultipartFile avatar);
}
