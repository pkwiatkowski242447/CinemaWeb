package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;

public class ClientRepositoryDeleteException extends ClientRepositoryException {
    public ClientRepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
