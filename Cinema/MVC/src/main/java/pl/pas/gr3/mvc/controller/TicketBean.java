package pl.pas.gr3.mvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.model.Ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class TicketBean implements Serializable {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger logger = LoggerFactory.getLogger(TicketBean.class);

    private final String restApiBaseURL = "http://localhost:8000/api/v1";

    private String ticketBaseURL;
    private String movieBaseURL;
    private String clientBaseURL;

    private final Ticket ticket = new Ticket();
    private final TicketInputDTO ticketInputDTO = new TicketInputDTO();

    private List<ClientDTO> listOfClientDTOs = new ArrayList<>();
    private List<MovieDTO> listOfMovieDTOs = new ArrayList<>();
    private List<TicketDTO> listOfTicketDTOs = new ArrayList<>();

    private ClientDTO clientDTO;
    private MovieDTO movieDTO;

    private int operationStatusCode = 0;
    private String message;

    @PostConstruct
    public void initializeData() {
        ticketBaseURL = restApiBaseURL + "/tickets";
        movieBaseURL = restApiBaseURL + "/movies";
        clientBaseURL = restApiBaseURL + "/clients";
    }

    // Create methods

    public String selectClient(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
        return "goToMovieSelection";
    }

    public String selectMovie(MovieDTO movieDTO) {
        this.movieDTO = movieDTO;
        return "goToTicketCreation";
    }

    public String createTicket() {
        ticket.setUserID(clientDTO.getClientID());
        ticket.setMovieID(movieDTO.getMovieID());

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String jsonPayload = objectMapper.writeValueAsString(ticket);
            String path = ticketBaseURL;

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            operationStatusCode = response.statusCode();

            if (operationStatusCode == 201) {
                return "ticketWasCreatedSuccessfully";
            } else {
                return "ticketCouldNotBeCreated";
            }
        } catch (JsonProcessingException exception) {
            return "ticketCouldNotBeCreated";
        }
    }

    // Read methods

    public List<ClientDTO> findAllClients() {
        String path = clientBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();
        return new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
    }

    public List<MovieDTO> findAllMovies() {
        String path = movieBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();
        return new ArrayList<>(response.jsonPath().getList(".", MovieDTO.class));
    }

    public List<TicketWithUserAndMovie> findAllTickets() {
        List<TicketWithUserAndMovie> listOfTickets = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Get all tickets
        String path = ticketBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        listOfTicketDTOs = new ArrayList<>(response.jsonPath().getList(".", TicketDTO.class));

        try {
            for (TicketDTO ticketDTO : listOfTicketDTOs) {

                path = clientBaseURL + "/" + ticketDTO.getClientID();

                requestSpecification = RestAssured.given();
                requestSpecification.accept(ContentType.JSON);

                response = requestSpecification.get(path);

                ClientDTO clientDTO = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);

                path = movieBaseURL + "/" + ticketDTO.getMovieID();

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

        return listOfTickets;
    }

    // Delete methods

    public void deleteTicket(TicketWithUserAndMovie ticket) {
        String path = clientBaseURL + "/" + ticket.getTicket().getTicketID();

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);

        this.findAllTickets();
    }
}
