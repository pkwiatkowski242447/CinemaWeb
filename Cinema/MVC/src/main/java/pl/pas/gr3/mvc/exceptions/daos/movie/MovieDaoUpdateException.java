package pl.pas.gr3.mvc.exceptions.daos.movie;

public class MovieDaoUpdateException extends GeneralMovieDaoException {
    public MovieDaoUpdateException(String message) {
        super(message);
    }

    public MovieDaoUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
