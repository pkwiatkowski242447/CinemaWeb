package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

public class UserRepositoryStaffNotFoundException extends UserRepositoryReadException {
    public UserRepositoryStaffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
