package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

public class UserRepositoryUserNotFoundException extends UserRepositoryReadException {
    public UserRepositoryUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
