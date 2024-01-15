package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

public class UserRepositoryAdminNotFoundException extends UserRepositoryReadException {
    public UserRepositoryAdminNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
