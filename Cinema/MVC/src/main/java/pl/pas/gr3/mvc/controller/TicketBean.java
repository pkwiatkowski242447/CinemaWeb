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
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@RequestScoped
@Named
public class TicketBean {

    private final String restApiBaseURL = "http://localhost:8000/api/v1";
    private String ticketBaseURL;
    private String movieBaseURL;
    private String clientBaseURL;
    private final Ticket ticket = new Ticket();
    private final TicketInputDTO ticketInputDTO = new TicketInputDTO();

    private List<TicketDTO> listOfTicketDTOs = new ArrayList<>();
    private List<ClientDTO> listOfClientDTOs = new ArrayList<>();
    private List<MovieDTO> listOfMovieDTOs = new ArrayList<>();

    private int operationStatusCode = 0;
    private String messages;

    @PostConstruct
    public void initializeData() {
        ticketBaseURL = restApiBaseURL + "/tickets";
        movieBaseURL = restApiBaseURL + "/movies";
        clientBaseURL = restApiBaseURL + "/clients";
        this.findAllTickets();
    }

    public void createTicket() {
        operationStatusCode = 400;
    }

    public void findAllTickets() {
        String path = ticketBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            listOfTicketDTOs = objectMapper.readValue(response.getBody().toString(), new TypeReference<List<TicketDTO>>() {});
        } catch (JsonProcessingException exception) {
            operationStatusCode = -1;
        }
    }

    public ClientDTO getClientForTicket(TicketDTO ticketDTO) throws JsonProcessingException {
        String path = clientBaseURL + "/" + ticketDTO.getClientID().toString();

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response.getBody().toString(), ClientDTO.class);
    }

    public MovieDTO getMovieForTicket(TicketDTO ticketDTO) throws JsonProcessingException {
        String path = movieBaseURL + "/" + ticketDTO.getTicketID().toString();

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(response.getBody().toString(), MovieDTO.class);
    }

    public void findAllClients() throws JsonProcessingException {
        String path = clientBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();
        listOfClientDTOs = new ArrayList<>(objectMapper.readValue(response.getBody().toString(), new TypeReference<List<ClientDTO>>() {}));
    }

    public void findAllMovies() throws JsonProcessingException {
        String path = movieBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();
        listOfMovieDTOs = new ArrayList<>(objectMapper.readValue(response.getBody().toString(), new TypeReference<List<MovieDTO>>() {}));
    }
}
