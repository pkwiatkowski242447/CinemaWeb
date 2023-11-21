package pl.pas.gr3.cinema.mapping.mappers;

import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.model.Movie;

public class MovieMapper {

    public static MovieDoc toMovieDoc(Movie movie) {
        MovieDoc movieDoc = new MovieDoc();
        movieDoc.setMovieID(movie.getMovieID());
        movieDoc.setMovieTitle(movie.getMovieTitle());
        movieDoc.setMovieBasePrice(movie.getMovieBasePrice());
        movieDoc.setScrRoomNumber(movie.getScrRoomNumber());
        movieDoc.setNumberOfAvailableSeats(movie.getNumberOfAvailableSeats());
        return movieDoc;
    }

    public static Movie toMovie(MovieDoc movieDoc) {
        return new Movie(movieDoc.getMovieID(),
                movieDoc.getMovieTitle(),
                movieDoc.getMovieBasePrice(),
                movieDoc.getScrRoomNumber(),
                movieDoc.getNumberOfAvailableSeats());
    }
}
