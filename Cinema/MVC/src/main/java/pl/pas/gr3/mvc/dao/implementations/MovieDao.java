package pl.pas.gr3.mvc.dao.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.MovieInputDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.dao.interfaces.IMovieDao;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoCreateException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoDeleteException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoUpdateException;

import java.util.ArrayList;
import java.util.List;

public class MovieDao implements IMovieDao {
    @Override
    public MovieDTO create(MovieInputDTO movieInputDTO) throws MovieDaoCreateException {
        String path = GeneralConstants.MOVIES_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        MovieDTO createdMovie;
        try {
            String jsonPayload = objectMapper.writeValueAsString(movieInputDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                createdMovie = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);
            } else {
                throw new MovieDaoCreateException("Podano nieprawidłowe dane.");
            }
        } catch (JsonProcessingException exception) {
            throw new MovieDaoCreateException("W trakcie tworzenia filmu doszło do błędu.");
        }
        return createdMovie;
    }

    @Override
    public MovieDTO readMovieForChange(MovieDTO movieDTO) throws MovieDaoReadException {
        String path = GeneralConstants.MOVIES_BASE_URL + "/" + movieDTO.getMovieID();
        ObjectMapper objectMapper = new ObjectMapper();
        MovieDTO selectedMovie;
        try {
            RequestSpecification requestSpecification = RestAssured.given();

            Response response = requestSpecification.get(path);

            if (response.statusCode() == 200) {
                selectedMovie = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);
            } else if (response.statusCode() == 404) {
                throw new MovieDaoReadException("Nie znaleziono wybranego filmu.");
            } else {
                throw new MovieDaoReadException("Nastąpił błąd podczas znajdowania wybranego filmu.");
            }
        } catch (JsonProcessingException exception) {
            throw new MovieDaoReadException(exception.getMessage(), exception);
        }
        return selectedMovie;
    }

    @Override
    public List<MovieDTO> findAll() throws MovieDaoReadException {
        List<MovieDTO> listOfMoviesDTOs;

        String path = GeneralConstants.MOVIES_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        if (response.statusCode() == 200) {
            listOfMoviesDTOs = new ArrayList<>(response.jsonPath().getList(".", MovieDTO.class));
        } else if (response.statusCode() == 404) {
            throw new MovieDaoReadException("Nie znaleziono żadnych filmów");
        } else {
            throw new MovieDaoReadException("Wystąpił błąd podczas pobierania filmów");
        }
        return listOfMoviesDTOs;
    }

    @Override
    public void updateMovie(MovieDTO movieDTO) throws MovieDaoUpdateException {
        String path = GeneralConstants.MOVIES_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonPayload = objectMapper.writeValueAsString(movieDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(path);

            if (response.statusCode() == 400) {
                throw new MovieDaoUpdateException("Podano nieprawidłowe dane");
            }
        } catch (JsonProcessingException exception) {
            throw new MovieDaoUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteMovie(MovieDTO movieDTO) throws MovieDaoDeleteException {
        String path = GeneralConstants.MOVIES_BASE_URL + "/" + movieDTO.getMovieID();

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);

        if (response.statusCode() == 400) {
            throw new MovieDaoDeleteException("Wystąpił błąd podczas usuwania filmu.");
        }
    }
}
