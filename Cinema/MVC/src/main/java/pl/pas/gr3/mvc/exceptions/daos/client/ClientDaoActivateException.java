package pl.pas.gr3.mvc.exceptions.daos.client;

public class ClientDaoActivateException extends GeneralClientDaoException {
    public ClientDaoActivateException(String message) {
        super(message);
    }

    public ClientDaoActivateException(String message, Throwable cause) {
        super(message, cause);
    }
}
