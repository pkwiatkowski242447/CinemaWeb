package pl.pas.gr3.mvc.exceptions.beans.clients;

public class ClientReadException extends ClientOperationException {
    public ClientReadException(String message) {
        super(message);
    }

    public ClientReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
