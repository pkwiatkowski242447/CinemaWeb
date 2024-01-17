package pl.pas.gr3.mvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.MovieInputDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class CreateMovieBean implements Serializable {

    private String movieTitle;
    private double movieBasePrice;
    private int screeningRoomNumber;
    private int numberOfAvailableSeats;

    private String message;
    private int operationStatus;

    private MovieDTO createdMovie;

    // Methods

    public String createMovie() {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = GeneralConstants.MOVIES_BASE_URL;
        try {
            String jsonPayload = objectMapper.writeValueAsString(new MovieInputDTO(movieTitle, movieBasePrice, screeningRoomNumber, numberOfAvailableSeats));

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                createdMovie = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);
                message = "Utworzono nowy film.";
                return "movieCreatedSuccessfully";
            } else {
                message = "Podano nieprawidłowe dane.";
                return null;
            }
        } catch (JsonProcessingException exception) {
            message = exception.getMessage();
            return null;
        }
    }
}
