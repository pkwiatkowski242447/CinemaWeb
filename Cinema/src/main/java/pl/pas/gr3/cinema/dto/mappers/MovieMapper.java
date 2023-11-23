package pl.pas.gr3.cinema.dto.mappers;

import pl.pas.gr3.cinema.dto.json.MovieJson;
import pl.pas.gr3.cinema.model.Movie;

public class MovieMapper {

    public static MovieJson toMovieJsonFromMovie(Movie movie) {
        MovieJson movieJson = new MovieJson();
        movieJson.setMovieID(movie.getMovieID());
        movieJson.setMovieTitle(movie.getMovieTitle());
        movieJson.setMovieBasePrice(movie.getMovieBasePrice());
        movieJson.setScreeningRoomNumber(movie.getScrRoomNumber());
        movieJson.setNumberOfAvailableSeats(movie.getNumberOfAvailableSeats());
        return movieJson;
    }

    public static Movie toMovieFromMovieJson(MovieJson movieJson) {
        return new Movie(movieJson.getMovieID(),
                movieJson.getMovieTitle(),
                movieJson.getMovieBasePrice(),
                movieJson.getScreeningRoomNumber(),
                movieJson.getNumberOfAvailableSeats());
    }
}
