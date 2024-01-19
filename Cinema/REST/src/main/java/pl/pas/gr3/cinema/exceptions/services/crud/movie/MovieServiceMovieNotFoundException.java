package pl.pas.gr3.cinema.exceptions.services.crud.movie;

public class MovieServiceMovieNotFoundException extends MovieServiceReadException {
    public MovieServiceMovieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
