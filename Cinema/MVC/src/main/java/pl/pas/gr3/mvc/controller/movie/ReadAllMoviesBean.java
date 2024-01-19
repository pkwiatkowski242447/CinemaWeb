package pl.pas.gr3.mvc.controller.movie;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.mvc.dao.implementations.MovieDao;
import pl.pas.gr3.mvc.dao.interfaces.IMovieDao;
import pl.pas.gr3.mvc.exceptions.beans.movies.MovieReadException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoReadException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ViewScoped
@Named
public class ReadAllMoviesBean implements Serializable {

    private List<MovieDTO> listOfMovieDTOs = new ArrayList<>();
    private String message;

    private IMovieDao movieDao;

    @Inject
    private MovieControllerBean movieControllerBean;

    @PostConstruct
    public void beanInit() {
        movieDao = new MovieDao();
        this.findAllMovies();
    }

    public void findAllMovies() {
        try {
            List<MovieDTO> listOfMovies = movieDao.findAll();
            if (listOfMovies.isEmpty()) {
                message = "Nie znaleziono żadnych filmów";
            }
            listOfMovieDTOs.clear();
            listOfMovieDTOs = listOfMovies;
        } catch (MovieDaoReadException exception) {
            message = exception.getMessage();
        }
    }

    public String updateMovie(MovieDTO movieDTO) {
        try {
            movieControllerBean.readMovieForChange(movieDTO);
            return "updateMovieAction";
        } catch (MovieReadException exception) {
            message = exception.getMessage();
            return null;
        }
    }

    public String deleteMovie(MovieDTO movieDTO) {
        try {
            movieControllerBean.readMovieForChange(movieDTO);
            return "deleteMovieAction";
        } catch (MovieReadException exception) {
            message = exception.getMessage();
            this.findAllMovies();
            return null;
        }
    }
}
