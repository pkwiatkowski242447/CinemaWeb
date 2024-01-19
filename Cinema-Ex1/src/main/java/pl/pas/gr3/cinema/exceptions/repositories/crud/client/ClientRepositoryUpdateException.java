package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;

public class ClientRepositoryUpdateException extends ClientRepositoryException {
    public ClientRepositoryUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
