package pl.pas.gr3.mvc.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pl.pas.gr3.mvc.constants.UserConstants;
import pl.pas.gr3.mvc.messages.UserValidationMessages;

@Data
public class User {

    //@NotNull(message = UserValidationMessages.NULL_LOGIN)
    //@Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    //@Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    private String clientLogin;

    //@NotNull(message = UserValidationMessages.NULL_PASSWORD)
    //@Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    //@Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    private String clientPassword;

    private boolean clientStatusActive;

    // Constructor

    public User() {

    }

    public User(@NotNull(message = UserValidationMessages.NULL_LOGIN)
                @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
                @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG) String clientLogin,
                @NotNull(message = UserValidationMessages.NULL_PASSWORD)
                @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
                @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG) String clientPassword,
                boolean clientStatusActive) {
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
        this.clientStatusActive = clientStatusActive;
    }
}
