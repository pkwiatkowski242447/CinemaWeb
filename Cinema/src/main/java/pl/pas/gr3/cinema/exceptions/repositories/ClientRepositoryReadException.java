package pl.pas.gr3.cinema.exceptions.repositories;

public class ClientRepositoryReadException extends ClientRepositoryException {
    public ClientRepositoryReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
