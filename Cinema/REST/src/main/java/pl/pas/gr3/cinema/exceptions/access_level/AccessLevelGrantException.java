package pl.pas.gr3.cinema.exceptions.access_level;

import pl.pas.gr3.cinema.utils.I18n;

public class AccessLevelGrantException extends AccessLevelBaseException {

    public AccessLevelGrantException() {
        super(I18n.ACCESS_LEVEL_GRANT_EXCEPTION);
    }

    public AccessLevelGrantException(String message) {
        super(message);
    }

    public AccessLevelGrantException(Throwable cause) {
        super(I18n.ACCESS_LEVEL_GRANT_EXCEPTION, cause);
    }

    public AccessLevelGrantException(String message, Throwable cause) {
        super(message, cause);
    }
}
