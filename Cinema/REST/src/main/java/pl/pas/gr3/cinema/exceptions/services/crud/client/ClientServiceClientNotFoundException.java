package pl.pas.gr3.cinema.exceptions.services.crud.client;

public class ClientServiceClientNotFoundException extends ClientServiceReadException {
    public ClientServiceClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
