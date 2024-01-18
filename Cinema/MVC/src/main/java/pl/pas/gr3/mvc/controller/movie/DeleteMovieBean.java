package pl.pas.gr3.mvc.controller.movie;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.mvc.exceptions.movies.MovieDeleteException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@RequestScoped
@Named
public class DeleteMovieBean implements Serializable {

    private MovieDTO movieDTO;

    @Inject
    private MovieControllerBean movieControllerBean;

    @PostConstruct
    public void beanInit() {
        movieDTO = movieControllerBean.getSelectedMovie();
    }

    // Actions

    public String deleteMovie() {
        try {
            movieControllerBean.deleteMovie(movieDTO);
            return "listOfAllMovies";
        } catch (MovieDeleteException exception) {
            movieControllerBean.setMessage(exception.getMessage());
            return "movieCouldNotBeDeleted";
        }
    }
}
