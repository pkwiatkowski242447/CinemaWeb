package pl.pas.gr3.mvc.exceptions.daos.client;

public class ClientDaoCreateException extends GeneralClientDaoException {
    public ClientDaoCreateException(String message) {
        super(message);
    }

    public ClientDaoCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
