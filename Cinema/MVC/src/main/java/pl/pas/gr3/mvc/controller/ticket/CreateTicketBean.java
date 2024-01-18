package pl.pas.gr3.mvc.controller.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.exceptions.tickets.TicketCreateException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class CreateTicketBean implements Serializable {

    private ClientDTO clientDTO;
    private MovieDTO movieDTO;
    private String movieDateTime;

    private String message;

    private List<ClientDTO> listOfClientDTOs;
    private List<MovieDTO> listOfMovieDTOs;

    @Inject
    private TicketControllerBean ticketControllerBean;

    @PostConstruct
    public void beanInit() {
        listOfClientDTOs = this.findAllClients();
        listOfMovieDTOs = this.findAllMovies();
        clientDTO = ticketControllerBean.getSelectedClientDTO();
        movieDTO = ticketControllerBean.getSelectedMovieDTO();
    }

    public String createTicket() {
        try {
            ticketControllerBean.createTicket(new TicketInputDTO(movieDateTime, clientDTO.getClientID(), movieDTO.getMovieID()));
            return "ticketCreatedSuccessfully";
        } catch (TicketCreateException exception) {
            message = exception.getMessage();
            return "ticketCouldNotBeCreated";
        }
    }

    public List<ClientDTO> findAllClients() {
        List<ClientDTO> listOfClientDTOs = new ArrayList<>();
        String path = GeneralConstants.CLIENTS_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();

        if (response.statusCode() == 200) {
            listOfClientDTOs = new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
        } else if (response.statusCode() == 404) {
            message = "Nie znaleziono żadnych klientów";
        } else {
            message = "Doszło do błędu podczas odczytu listy klientów";
        }
        return listOfClientDTOs;
    }

    public List<MovieDTO> findAllMovies() {
        List<MovieDTO> listOfMoviesDTOs = new ArrayList<>();
        String path = GeneralConstants.MOVIES_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ObjectMapper objectMapper = new ObjectMapper();

        if (response.statusCode() == 200) {
            listOfMoviesDTOs = new ArrayList<>(response.jsonPath().getList(".", MovieDTO.class));
        } else if (response.statusCode() == 404) {
            message = "Nie znaleziono żadnych filmów";
        } else {
            message = "Doszło do błędu podczas odczytu listy filmów";
        }
        return listOfMoviesDTOs;
    }

    public String selectClient(ClientDTO clientDTO) {
        ticketControllerBean.setSelectedClientDTO(clientDTO);
        return "goToMovieSelection";
    }

    public String selectMovie(MovieDTO movieDTO) {
        ticketControllerBean.setSelectedMovieDTO(movieDTO);
        return "goToTicketDetails";
    }
}
