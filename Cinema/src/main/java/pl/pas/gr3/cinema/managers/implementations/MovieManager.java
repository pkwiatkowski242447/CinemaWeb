package pl.pas.gr3.cinema.managers.implementations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerUpdateException;
import pl.pas.gr3.cinema.exceptions.repositories.MovieRepositoryException;
import pl.pas.gr3.cinema.managers.interfaces.MovieManagerInterface;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;

import java.io.Closeable;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MovieManager implements MovieManagerInterface, Closeable {

    @Inject
    private MovieRepository movieRepository;

    public MovieManager() {
    }

    public MovieManager(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) throws MovieManagerCreateException {
        try {
            return this.movieRepository.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        } catch (MovieRepositoryException exception) {
            throw new MovieManagerCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Movie findByUUID(UUID movieID) throws MovieManagerReadException {
        try {
            return this.movieRepository.findByUUID(movieID);
        } catch (MovieRepositoryException exception) {
            throw new MovieManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Movie> findAll() throws MovieManagerReadException {
        try {
            return this.movieRepository.findAll();
        } catch (MovieRepositoryException exception) {
            throw new MovieManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Movie movie) throws MovieManagerUpdateException {
        try {
            this.movieRepository.update(movie);
        } catch (MovieRepositoryException exception) {
            throw new MovieManagerUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID movieID) throws MovieManagerDeleteException {
        try {
            this.movieRepository.update(this.movieRepository.findByUUID(movieID));
        } catch (MovieRepositoryException exception) {
            throw new MovieManagerDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        this.movieRepository.close();
    }
}
