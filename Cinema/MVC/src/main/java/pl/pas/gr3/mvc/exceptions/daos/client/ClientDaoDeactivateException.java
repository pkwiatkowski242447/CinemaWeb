package pl.pas.gr3.mvc.exceptions.daos.client;

public class ClientDaoDeactivateException extends GeneralClientDaoException {
    public ClientDaoDeactivateException(String message) {
        super(message);
    }

    public ClientDaoDeactivateException(String message, Throwable cause) {
        super(message, cause);
    }
}
