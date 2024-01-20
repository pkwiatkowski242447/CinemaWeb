package pl.pas.gr3.mvc.controller.movie;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.MovieDTO;
import pl.pas.gr3.mvc.exceptions.beans.movies.MovieUpdateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@RequestScoped
@Named
public class UpdateMovieBean implements Serializable {

    private MovieDTO movieDTO;
    private String message;

    @Inject
    private MovieControllerBean movieControllerBean;

    @PostConstruct
    public void beanInit() {
        movieDTO = movieControllerBean.getSelectedMovie();
    }

    public String updateMovie() {
        try {
            movieControllerBean.updateMovie(movieDTO);
            return "listOfAllMovies";
        } catch (MovieUpdateException exception) {
            message = exception.getMessage();
            return null;
        }
    }
}
