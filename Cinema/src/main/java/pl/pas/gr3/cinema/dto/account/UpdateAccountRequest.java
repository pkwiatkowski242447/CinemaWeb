package pl.pas.gr3.cinema.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pl.pas.gr3.cinema.util.consts.AccountMessages;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;

import java.util.UUID;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest extends AccountSignatureResponse {

    @NotBlank(message = AccountMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = AccountMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = AccountMessages.LOGIN_TOO_LONG)
    private String login;

    @NotBlank(message = AccountMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = AccountMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = AccountMessages.PASSWORD_TOO_LONG)
    private String password;

    private boolean active;
}
