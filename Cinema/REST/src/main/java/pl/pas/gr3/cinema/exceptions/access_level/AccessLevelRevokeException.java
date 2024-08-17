package pl.pas.gr3.cinema.exceptions.access_level;

import pl.pas.gr3.cinema.utils.I18n;

public class AccessLevelRevokeException extends AccessLevelBaseException {

    public AccessLevelRevokeException() {
        super(I18n.ACCESS_LEVEL_REVOKE_EXCEPTION);
    }

    public AccessLevelRevokeException(String message) {
        super(message);
    }

    public AccessLevelRevokeException(Throwable cause) {
        super(I18n.ACCESS_LEVEL_REVOKE_EXCEPTION, cause);
    }

    public AccessLevelRevokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
