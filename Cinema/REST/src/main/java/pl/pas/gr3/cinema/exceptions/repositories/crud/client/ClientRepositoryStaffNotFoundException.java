package pl.pas.gr3.cinema.exceptions.repositories.crud.client;

public class ClientRepositoryStaffNotFoundException extends ClientRepositoryReadException {
    public ClientRepositoryStaffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
