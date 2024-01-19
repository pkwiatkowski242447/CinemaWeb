package pl.pas.gr3.mvc.exceptions.daos.client;

public class ClientDaoUpdateException extends GeneralClientDaoException {
    public ClientDaoUpdateException(String message) {
        super(message);
    }

    public ClientDaoUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
