package pl.pas.gr3.cinema.exceptions.repositories.crud.movie;

public class MovieRepositoryMovieNotFoundException extends MovieRepositoryReadException {
    public MovieRepositoryMovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
