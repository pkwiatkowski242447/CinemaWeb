package pl.pas.gr3.mvc.exceptions.daos.client;

public class ClientDaoReadException extends GeneralClientDaoException {
    public ClientDaoReadException(String message) {
        super(message);
    }

    public ClientDaoReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
