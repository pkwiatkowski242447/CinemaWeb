package pl.pas.gr3.mvc.exceptions.beans.clients;

public class ClientOperationException extends Exception {
    public ClientOperationException(String message) {
        super(message);
    }

    public ClientOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
