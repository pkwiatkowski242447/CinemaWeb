package pl.pas.gr3.cinema.exceptions.repositories.crud.user;

public class UserRepositoryCreateUserDuplicateLoginException extends UserRepositoryCreateException {

    public UserRepositoryCreateUserDuplicateLoginException(String message) {
        super(message);
    }
}
