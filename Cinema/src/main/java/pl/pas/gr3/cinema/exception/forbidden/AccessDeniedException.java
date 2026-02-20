package pl.pas.gr3.cinema.exception.forbidden;

import pl.pas.gr3.cinema.exception.general.ForbiddenException;
import pl.pas.gr3.cinema.util.I18n;

public class AccessDeniedException extends ForbiddenException {

    public AccessDeniedException() {
        super(I18n.APP_ACCESS_DENIED);
    }
}
