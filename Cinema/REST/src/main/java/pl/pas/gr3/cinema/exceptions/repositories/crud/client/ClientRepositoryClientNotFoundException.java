package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

public class ClientRepositoryClientNotFoundException extends ClientRepositoryReadException {
    public ClientRepositoryClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
