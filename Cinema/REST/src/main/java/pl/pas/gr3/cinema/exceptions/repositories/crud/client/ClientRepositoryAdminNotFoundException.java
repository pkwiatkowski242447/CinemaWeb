package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

public class ClientRepositoryAdminNotFoundException extends ClientRepositoryReadException {
    public ClientRepositoryAdminNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
