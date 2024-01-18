package pl.pas.gr3.mvc.controller.movie;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.exceptions.movies.MovieReadException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class ReadAllMoviesBean implements Serializable {

    private List<MovieDTO> listOfMovieDTOs;
    private String message;
    private int operationStatusCode;

    @Inject
    private MovieControllerBean movieControllerBean;

    @PostConstruct
    public void beanInit() {
        listOfMovieDTOs = this.findAllMovies();
    }

    public List<MovieDTO> findAllMovies() {
        List<MovieDTO> listOfMoviesDTOs = new ArrayList<>();

        String path = GeneralConstants.MOVIES_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        if (response.statusCode() == 200) {
            listOfMoviesDTOs = new ArrayList<>(response.jsonPath().getList(".", MovieDTO.class));
        } else if (response.statusCode() == 404) {
            message = "Nie znaleziono żadnych filmów";
        } else {
            message = "Wystąpił błąd podczas pobierania filmów";
        }
        return listOfMoviesDTOs;
    }

    public String deleteMovie(MovieDTO movieDTO) {
        try {
            movieControllerBean.readMovieForChange(movieDTO);
            return "deleteMovieAction";
        } catch (MovieReadException exception) {
            message = exception.getMessage();
            return null;
        }
    }
}
