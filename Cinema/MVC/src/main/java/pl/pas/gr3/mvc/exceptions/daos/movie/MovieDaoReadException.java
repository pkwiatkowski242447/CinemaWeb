package pl.pas.gr3.mvc.exceptions.daos.movie;

public class MovieDaoReadException extends GeneralMovieDaoException {
    public MovieDaoReadException(String message) {
        super(message);
    }

    public MovieDaoReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
