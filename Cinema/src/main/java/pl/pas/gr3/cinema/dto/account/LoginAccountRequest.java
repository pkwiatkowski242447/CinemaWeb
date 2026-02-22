package pl.pas.gr3.cinema.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.pas.gr3.cinema.util.consts.AccountMessages;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;

public record LoginAccountRequest(

    @NotBlank(message = AccountMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = AccountMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = AccountMessages.LOGIN_TOO_LONG)
    String login,

    @NotBlank(message = AccountMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = AccountMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = AccountMessages.PASSWORD_TOO_LONG)
    String password
) {}
