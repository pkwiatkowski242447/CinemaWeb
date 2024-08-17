package pl.pas.gr3.cinema.controllers.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.pas.gr3.cinema.common.dto.input.AccountRegisterDTO;

@RequestMapping(path = "/api/v1/register")
public interface IRegistrationController {

    @Operation(method = "POST", summary = "Create account with client access level",
            description = "This endpoint can be used to create new account with default access level of client.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data transfer object containing all the data required to create new account.",
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = AccountRegisterDTO.class)
            )
    )
    @ApiResponses(
            @ApiResponse(
                    responseCode = "201",
                    description = "User account with client access level was created successfully.",
                    headers = @Header(
                            name = HttpHeaders.LOCATION,
                            description = "URL of the new user account."
                    )
            )
    )
    @PostMapping(path = "/client", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> registerClient(@ModelAttribute AccountRegisterDTO accountRegisterDTO);

    @Operation(method = "POST", summary = "Create account with staff access level",
            description = "This endpoint can be used to create new account with default access level of staff.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data transfer object containing all the data required to create new account.",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountRegisterDTO.class)
            )
    )
    @ApiResponses(
        @ApiResponse(
                responseCode = "201",
                description = "User account with staff access level was created successfully.",
                headers = @Header(
                        name = HttpHeaders.LOCATION,
                        description = "URL of the new user account."
                )
        )
    )
    @PostMapping(path = "/staff", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> registerStaff(@RequestBody AccountRegisterDTO accountRegisterDTO);

    @Operation(method = "POST", summary = "Create account with admin access level",
            description = "This endpoint can be used to create new account with default access level of admin.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data transfer object containing all the data required to create new account.",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountRegisterDTO.class)
            )
    )
    @ApiResponses(
            @ApiResponse(
                    responseCode = "201",
                    description = "User account with admin access level was created successfully.",
                    headers = @Header(
                            name = HttpHeaders.LOCATION,
                            description = "URL of the new user account."
                    )
            )
    )
    @PostMapping(path = "/admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> registerAdmin(@RequestBody AccountRegisterDTO accountRegisterDTO);
}
