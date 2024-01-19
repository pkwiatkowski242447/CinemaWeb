package pl.pas.gr3.cinema.exceptions.repositories;

public class ClientRepositoryException extends GeneralRepositoryException {
    public ClientRepositoryException(String message) {
        super(message);
    }

    public ClientRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
