package pl.pas.gr3.mvc.exceptions.daos.movie;

import pl.pas.gr3.mvc.exceptions.daos.GeneralDaoException;

public class GeneralMovieDaoException extends GeneralDaoException {
    public GeneralMovieDaoException(String message) {
        super(message);
    }

    public GeneralMovieDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
