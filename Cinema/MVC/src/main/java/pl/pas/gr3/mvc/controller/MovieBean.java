package pl.pas.gr3.mvc.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.model.Movie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class MovieBean implements Serializable {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger logger = LoggerFactory.getLogger(MovieBean.class);

    private List<MovieDTO> listOfMovieDTOs = new ArrayList<>();

    private int operationStatusCode;
    private String message;

    @PostConstruct
    public void initializeData () {
        this.findAllMovies();
    }

    // Create methods

    // Read methods

    public void findAllMovies() {
        String path = GeneralConstants.MOVIES_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        if (response.statusCode() == 200) {
            listOfMovieDTOs = new ArrayList<>(response.jsonPath().getList(".", MovieDTO.class));
            message = response.getBody().asString();
        } else if (response.statusCode() == 404) {
            message = "Nie znaleziono żadnych filmów";
        } else {
            message = response.getBody().asString();
        }
    }

    // Delete methods

    public String deleteMovie(MovieDTO movieDTO) {
        String path = GeneralConstants.MOVIES_BASE_URL + "/" + movieDTO.getMovieID();

        logger.error(path);

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        operationStatusCode = response.statusCode();

        if (response.statusCode() == 204) {
            message = "Film został usunięty.";
            return null;
        } else {
            message = response.getBody().asString();
            return "movieCouldNotBeDeleted";
        }
    }
}
