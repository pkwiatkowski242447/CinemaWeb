package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class MovieDeleteException extends BadRequestException {

    public MovieDeleteException() {
        super(I18n.MOVIE_DELETE);
    }

    public MovieDeleteException(Throwable cause) {
        super(I18n.MOVIE_DELETE, cause);
    }
}
