package pl.pas.gr3.mvc.exceptions.daos.movie;

public class MovieDaoCreateException extends GeneralMovieDaoException {
    public MovieDaoCreateException(String message) {
        super(message);
    }

    public MovieDaoCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
