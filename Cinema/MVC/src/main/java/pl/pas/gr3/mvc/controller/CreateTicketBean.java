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
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class CreateTicketBean implements Serializable {

    private ClientDTO clientDTO;
    private MovieDTO movieDTO;
    private LocalDate movieDate;
    private LocalTime movieTime;
    private LocalDateTime movieDateTime;

    private TicketDTO createdTicket;

    private String message;
    private int operationStatusCode;

    // Create methods

    public String createTicket() {
        String path = GeneralConstants.TICKETS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();

        // LocalDateTime localDateTime = LocalDateTime.of(movieDate, movieTime);
        try {
            String jsonPayload = objectMapper.writeValueAsString(new TicketInputDTO(movieDateTime.toString(), clientDTO.getClientID(), movieDTO.getMovieID()));
            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                createdTicket = objectMapper.readValue(response.getBody().asString(), TicketDTO.class);
                return "ticketWasCreatedSuccessfully";
            } else {
                // message = response.getBody().asString();
                message = "Wprowadzono błędne dane";
                return null;
            }

        } catch (JsonProcessingException exception) {
            message = exception.getMessage();
            message = "Some exception";
            return null;
        }
    }

    // Read methods

    public List<ClientDTO> findAllClients() {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();
        return new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
    }

    public List<MovieDTO> findAllMovies() {
        String path = GeneralConstants.MOVIES_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();
        return new ArrayList<>(response.jsonPath().getList(".", MovieDTO.class));
    }
}
