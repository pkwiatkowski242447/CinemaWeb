package pl.pas.gr3.mvc.exceptions.daos.movie;

public class MovieDaoDeleteException extends GeneralMovieDaoException {
    public MovieDaoDeleteException(String message) {
        super(message);
    }

    public MovieDaoDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
