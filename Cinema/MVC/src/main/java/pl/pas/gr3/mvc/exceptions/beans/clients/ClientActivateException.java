package pl.pas.gr3.mvc.exceptions.beans.clients;

public class ClientActivateException extends ClientOperationException {
    public ClientActivateException(String message) {
        super(message);
    }

    public ClientActivateException(String message, Throwable cause) {
        super(message, cause);
    }
}
