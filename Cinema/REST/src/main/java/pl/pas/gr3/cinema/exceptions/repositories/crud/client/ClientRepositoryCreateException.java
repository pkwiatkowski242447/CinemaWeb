package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;

public class ClientRepositoryCreateException extends ClientRepositoryException {

    public ClientRepositoryCreateException(String message) {
        super(message);
    }

    public ClientRepositoryCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
