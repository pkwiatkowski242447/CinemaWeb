package pl.pas.gr3.mvc.controller.ticket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.exceptions.tickets.TicketCreateException;
import pl.pas.gr3.mvc.exceptions.tickets.TicketDeleteException;
import pl.pas.gr3.mvc.exceptions.tickets.TicketReadException;
import pl.pas.gr3.mvc.exceptions.tickets.TicketUpdateException;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@SessionScoped
@Named
public class TicketControllerBean implements Serializable {

    private TicketWithUserAndMovie createdTicket;
    private TicketWithUserAndMovie selectedTicket;

    private ClientDTO selectedClientDTO;
    private MovieDTO selectedMovieDTO;

    private String message;

    // Actions

    public void createTicket(TicketInputDTO ticketInputDTO) throws TicketCreateException {
        String path = GeneralConstants.TICKETS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            String jsonPayload = objectMapper.writeValueAsString(ticketInputDTO);
            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                TicketDTO ticketDTO = objectMapper.readValue(response.getBody().asString(), TicketDTO.class);

                response = requestSpecification.get(GeneralConstants.CLIENTS_BASE_URL + "/" + ticketDTO.getClientID());
                ClientDTO clientDTO = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);

                response = requestSpecification.get(GeneralConstants.MOVIES_BASE_URL + "/" + ticketDTO.getMovieID());
                MovieDTO movieDTO = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);

                createdTicket = new TicketWithUserAndMovie(ticketDTO, clientDTO, movieDTO);
            } else {
                throw new TicketCreateException("Wprowadzono błędne dane");
            }

        } catch (JsonProcessingException exception) {
            throw new TicketCreateException("W trakcie tworzenia biletu doszło do błędu");
        }
    }

    public void readTicketForChanges(TicketDTO ticketDTO) throws TicketReadException{
        String path = GeneralConstants.TICKETS_BASE_URL + "/" + ticketDTO.getTicketID();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            RequestSpecification requestSpecification = RestAssured.given();

            Response response = requestSpecification.get(path);

            if (response.statusCode() == 200) {
                TicketDTO ticket = objectMapper.readValue(response.getBody().asString(), TicketDTO.class);

                response = requestSpecification.get(GeneralConstants.CLIENTS_BASE_URL + "/" + ticket.getClientID());
                ClientDTO clientDTO = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);

                response = requestSpecification.get(GeneralConstants.MOVIES_BASE_URL + "/" + ticket.getMovieID());
                MovieDTO movieDTO = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);

                selectedTicket = new TicketWithUserAndMovie(ticket, clientDTO, movieDTO);
            } else if (response.statusCode() == 404) {
                throw new TicketReadException("Nie znaleziono wybranego biletu.");
            } else {
                throw new TicketReadException("Nastąpił błąd podczas znajdowania wybranego biletu.");
            }
        } catch (JsonProcessingException exception) {
            throw new TicketReadException(exception.getMessage(), exception);
        }
    }

    public void updateTicket(TicketDTO ticketDTO) throws TicketUpdateException {
        String path = GeneralConstants.TICKETS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            String jsonPayload = objectMapper.writeValueAsString(ticketDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(path);

            if (response.statusCode() == 400) {
                throw new TicketUpdateException("Podano nieprawidłowe dane");
            }
        } catch (JsonProcessingException exception) {
            throw new TicketUpdateException(exception.getMessage(), exception);
        }
    }

    public void deleteTicket(TicketDTO ticketDTO) throws TicketDeleteException {
        String path = GeneralConstants.TICKETS_BASE_URL + "/" + ticketDTO.getTicketID();

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);

        String responseBody = response.getBody().asString();

        if (response.statusCode() == 400) {
            throw new TicketDeleteException("Wystąpił błąd podczas usuwania biletu.");
        }
    }
}
