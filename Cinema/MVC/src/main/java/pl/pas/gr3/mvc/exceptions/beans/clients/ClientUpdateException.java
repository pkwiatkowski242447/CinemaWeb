package pl.pas.gr3.mvc.exceptions.beans.clients;

public class ClientUpdateException extends ClientOperationException {
    public ClientUpdateException(String message) {
        super(message);
    }

    public ClientUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
