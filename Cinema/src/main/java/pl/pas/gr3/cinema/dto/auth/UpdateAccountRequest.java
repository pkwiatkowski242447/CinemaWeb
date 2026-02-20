package pl.pas.gr3.cinema.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.pas.gr3.cinema.dto.messages.UserMessages;
import pl.pas.gr3.cinema.dto.validation.UserConstants;

import java.util.UUID;

public record UpdateAccountRequest(
    @NotNull(message = UserMessages.NULL_IDENTIFIER)
    UUID id,

    @NotBlank(message = UserMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserMessages.LOGIN_TOO_LONG)
    String login,

    @NotBlank(message = UserMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserMessages.PASSWORD_TOO_LONG)
    String password,

    boolean active
) {}
