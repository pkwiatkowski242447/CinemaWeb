package pl.pas.gr3.mvc.exceptions.daos;

public class GeneralDaoException extends Exception {
    public GeneralDaoException(String message) {
        super(message);
    }

    public GeneralDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
