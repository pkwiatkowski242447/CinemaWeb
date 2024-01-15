package pl.pas.gr3.mvc.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.mvc.constants.ClientConstants;
import pl.pas.gr3.mvc.messages.ClientValidationMessages;

@Getter @Setter
@NoArgsConstructor
public class Client {

    @NotNull(message = ClientValidationMessages.NULL_LOGIN)
    @Size(min = ClientConstants.LOGIN_MIN_LENGTH, message = ClientValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = ClientConstants.LOGIN_MAX_LENGTH, message = ClientValidationMessages.LOGIN_TOO_LONG)
    private String clientLogin;

    @NotNull(message = ClientValidationMessages.NULL_PASSWORD)
    @Size(min = ClientConstants.PASSWORD_MIN_LENGTH, message = ClientValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = ClientConstants.PASSWORD_MAX_LENGTH, message = ClientValidationMessages.PASSWORD_TOO_LONG)
    private String clientPassword;

    private boolean clientStatusActive;

    public Client(@NotNull(message = ClientValidationMessages.NULL_LOGIN)
                  @Size(min = ClientConstants.LOGIN_MIN_LENGTH, message = ClientValidationMessages.LOGIN_TOO_SHORT)
                  @Size(max = ClientConstants.LOGIN_MAX_LENGTH, message = ClientValidationMessages.LOGIN_TOO_LONG) String clientLogin,
                  @NotNull(message = ClientValidationMessages.NULL_PASSWORD)
                  @Size(min = ClientConstants.PASSWORD_MIN_LENGTH, message = ClientValidationMessages.PASSWORD_TOO_SHORT)
                  @Size(max = ClientConstants.PASSWORD_MAX_LENGTH, message = ClientValidationMessages.PASSWORD_TOO_LONG) String clientPassword,
                  boolean clientStatusActive) {
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
        this.clientStatusActive = clientStatusActive;
    }
}
