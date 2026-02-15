package pl.pas.gr3.cinema.exception.repositories.crud.user;

public class UserRepositoryUserNotFoundException extends UserRepositoryReadException {
    public UserRepositoryUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
