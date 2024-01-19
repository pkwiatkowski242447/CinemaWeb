package pl.pas.gr3.mvc.controller.movie;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.MovieInputDTO;
import pl.pas.gr3.mvc.dao.implementations.MovieDao;
import pl.pas.gr3.mvc.dao.interfaces.IMovieDao;
import pl.pas.gr3.mvc.exceptions.beans.movies.MovieCreateException;
import pl.pas.gr3.mvc.exceptions.beans.movies.MovieDeleteException;
import pl.pas.gr3.mvc.exceptions.beans.movies.MovieReadException;
import pl.pas.gr3.mvc.exceptions.beans.movies.MovieUpdateException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoCreateException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoDeleteException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoUpdateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@SessionScoped
@Named
public class MovieControllerBean implements Serializable {

    // Properties

    private MovieDTO createdMovie;
    private MovieDTO selectedMovie;

    private String message;

    private IMovieDao movieDao;

    @PostConstruct
    public void beanInit() {
        movieDao = new MovieDao();
    }

    // Actions

    // Create methods

    public void createMovie(MovieInputDTO movieInputDTO) throws MovieCreateException {
        try {
            createdMovie = movieDao.create(movieInputDTO);
        } catch (MovieDaoCreateException exception) {
            throw new MovieCreateException(exception.getMessage());
        }
    }

    // Read methods

    public void readMovieForChange(MovieDTO movieDTO) throws MovieReadException {
        try {
            selectedMovie = movieDao.readMovieForChange(movieDTO);
        } catch (MovieDaoReadException exception) {
            throw new MovieReadException(exception.getMessage(), exception);
        }
    }

    // Update methods

    public void updateMovie(MovieDTO movieDTO) throws MovieUpdateException {
        try {
            movieDao.updateMovie(movieDTO);
        } catch (MovieDaoUpdateException exception) {
            throw new MovieUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    public void deleteMovie(MovieDTO movieDTO) throws MovieDeleteException {
        try {
            movieDao.deleteMovie(movieDTO);
        } catch (MovieDaoDeleteException exception) {
            throw new MovieDeleteException(exception.getMessage());
        }
    }
}
