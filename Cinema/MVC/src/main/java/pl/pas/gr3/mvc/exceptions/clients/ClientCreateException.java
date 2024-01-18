package pl.pas.gr3.mvc.exceptions.clients;

public class ClientCreateException extends ClientOperationException {
    public ClientCreateException(String message) {
        super(message);
    }

    public ClientCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
