package pl.pas.gr3.mvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class TicketBean implements Serializable {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger logger = LoggerFactory.getLogger(TicketBean.class);

    private List<TicketDTO> listOfTicketDTOs = new ArrayList<>();

    private List<TicketWithUserAndMovie> listOfTickets = new ArrayList<>();

    private int operationStatusCode;
    private String message;

    @PostConstruct
    public void initializeData() {
        this.findAllTickets();
    }

    // Read methods

    public void findAllTickets() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Get all tickets
        String path = GeneralConstants.TICKETS_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        if (response.statusCode() == 200) {
            listOfTicketDTOs = new ArrayList<>(response.jsonPath().getList(".", TicketDTO.class));

            try {
                for (TicketDTO ticketDTO : listOfTicketDTOs) {

                    path = GeneralConstants.CLIENTS_BASE_URL + "/" + ticketDTO.getClientID();

                    requestSpecification = RestAssured.given();
                    requestSpecification.accept(ContentType.JSON);

                    response = requestSpecification.get(path);

                    ClientDTO clientDTO = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);

                    path = GeneralConstants.MOVIES_BASE_URL + "/" + ticketDTO.getMovieID();

                    requestSpecification = RestAssured.given();
                    requestSpecification.accept(ContentType.JSON);

                    response = requestSpecification.get(path);

                    MovieDTO movieDTO = objectMapper.readValue(response.getBody().asString(), MovieDTO.class);

                    listOfTickets.add(new TicketWithUserAndMovie(ticketDTO, clientDTO, movieDTO));
                }
            } catch (JsonProcessingException exception) {
                operationStatusCode = -1;
                message = exception.getMessage();
            }
        } else {
            message = "Nie znaleziono żadnych biletów";
        }
    }

    // Delete methods

    public String deleteTicket(TicketWithUserAndMovie ticket) {
        String path = GeneralConstants.TICKETS_BASE_URL + "/" + ticket.getTicket().getTicketID();

        logger.error(path);

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        operationStatusCode = response.statusCode();

        if (response.statusCode() == 204) {
            message = "Bilet został usunięty";
            return null;
        } else {
            message = response.getBody().asString();
            return "ticketCouldNotBeDeleted";
        }
    }
}
