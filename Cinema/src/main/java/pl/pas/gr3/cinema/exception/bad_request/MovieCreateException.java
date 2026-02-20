package pl.pas.gr3.cinema.exception.bad_request;

import pl.pas.gr3.cinema.exception.general.BadRequestException;
import pl.pas.gr3.cinema.util.I18n;

public class MovieCreateException extends BadRequestException {

    public MovieCreateException() {
        super(I18n.MOVIE_CREATE);
    }

    public MovieCreateException(Throwable throwable) {
        super(I18n.MOVIE_CREATE, throwable);
    }
}
