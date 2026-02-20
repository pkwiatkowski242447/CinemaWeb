package pl.pas.gr3.cinema.exception.not_found;

import pl.pas.gr3.cinema.exception.general.NotFoundException;
import pl.pas.gr3.cinema.util.I18n;

public class MovieNotFoundException extends NotFoundException {

    public MovieNotFoundException() {
        super(I18n.MOVIE_NOT_FOUND);
    }

    public MovieNotFoundException(Throwable throwable) {
        super(I18n.MOVIE_NOT_FOUND, throwable);
    }
}
