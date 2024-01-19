package pl.pas.gr3.mvc.exceptions.beans.clients;

public class ClientDeactivateException extends ClientOperationException {
    public ClientDeactivateException(String message) {
        super(message);
    }

    public ClientDeactivateException(String message, Throwable cause) {
        super(message, cause);
    }
}
