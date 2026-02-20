package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class MovieUpdateException extends BadRequestException {

    public MovieUpdateException() {
        super(I18n.MOVIE_UPDATE);
    }

    public MovieUpdateException(Throwable cause) {
        super(I18n.MOVIE_UPDATE, cause);
    }
}
