package pl.pas.gr3.mvc.exceptions.daos.client;

import pl.pas.gr3.mvc.exceptions.daos.GeneralDaoException;

public class GeneralClientDaoException extends GeneralDaoException {
    public GeneralClientDaoException(String message) {
        super(message);
    }

    public GeneralClientDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
