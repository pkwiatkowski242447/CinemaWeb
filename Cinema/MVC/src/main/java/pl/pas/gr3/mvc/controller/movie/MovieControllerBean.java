package pl.pas.gr3.mvc.controller.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.MovieInputDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.exceptions.movies.MovieCreateException;
import pl.pas.gr3.mvc.exceptions.movies.MovieDeleteException;
import pl.pas.gr3.mvc.exceptions.movies.MovieReadException;
import pl.pas.gr3.mvc.exceptions.movies.MovieUpdateException;

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

    // Actions

    // Create methods

    public void createMovie(MovieInputDTO movieInputDTO) throws MovieCreateException {
        String path = GeneralConstants.MOVIES_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(movieInputDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                createdMovie = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);
            } else {
                throw new MovieCreateException("Podano nieprawidłowe dane.");
            }
        } catch (JsonProcessingException exception) {
            throw new MovieCreateException("W trakcie tworzenia filmu doszło do błędu.");
        }
    }

    // Read methods

    public void readMovieForChange(MovieDTO movieDTO) throws MovieReadException {
        String path = GeneralConstants.MOVIES_BASE_URL + "/" + movieDTO.getMovieID();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            RequestSpecification requestSpecification = RestAssured.given();

            Response response = requestSpecification.get(path);

            if (response.statusCode() == 200) {
                selectedMovie = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);
            } else if (response.statusCode() == 404) {
                throw new MovieReadException("Nie znaleziono wybranego filmu.");
            } else {
                throw new MovieReadException("Nastąpił błąd podczas znajdowania wybranego filmu.");
            }
        } catch (JsonProcessingException exception) {
            throw new MovieReadException(exception.getMessage(), exception);
        }
    }

    // Update methods

    public void updateMovie(MovieDTO movieDTO) throws MovieUpdateException {
        String path = GeneralConstants.MOVIES_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonPayload = objectMapper.writeValueAsString(movieDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(path);

            if (response.statusCode() == 400) {
                throw new MovieUpdateException("Podano nieprawidłowe dane");
            }
        } catch (JsonProcessingException exception) {
            throw new MovieUpdateException(exception.getMessage(), exception);
        }
    }

    // Delete methods

    public void deleteMovie(MovieDTO movieDTO) throws MovieDeleteException {
        String path = GeneralConstants.MOVIES_BASE_URL + "/" + movieDTO.getMovieID();

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);

        if (response.statusCode() == 400) {
            throw new MovieDeleteException("Wystąpił błąd podczas usuwania filmu.");
        }
    }
}
