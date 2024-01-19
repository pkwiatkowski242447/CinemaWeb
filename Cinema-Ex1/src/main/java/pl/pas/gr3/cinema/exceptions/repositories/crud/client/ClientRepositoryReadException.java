package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;

public class ClientRepositoryReadException extends ClientRepositoryException {
    public ClientRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
