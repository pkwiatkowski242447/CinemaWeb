package pl.pas.gr3.mvc.controller.ticket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.exceptions.tickets.TicketReadException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class ReadAllTicketsBean implements Serializable {

    private List<TicketWithUserAndMovie> listOfExtTickets;

    private int operationStatusCode;
    private String message;

    @Inject
    private TicketControllerBean ticketControllerBean;

    @PostConstruct
    public void beanInit() {
        listOfExtTickets = this.findAllTickets();
    }

    public List<TicketWithUserAndMovie> findAllTickets() {
        List<TicketDTO> listOfTicketDTOs = new ArrayList<>();
        List<TicketWithUserAndMovie> listOfFoundTickets = new ArrayList<>();

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

                    listOfFoundTickets.add(new TicketWithUserAndMovie(ticketDTO, clientDTO, movieDTO));
                }
            } catch (JsonProcessingException exception) {
                operationStatusCode = -1;
                message = exception.getMessage();
            }
        } else {
            message = "Nie znaleziono żadnych biletów";
        }
        return listOfFoundTickets;
    }

    public String deleteTicket(TicketDTO ticketDTO) {
        try {
            ticketControllerBean.readTicketForChanges(ticketDTO);
            return "deleteTicketAction";
        } catch (TicketReadException exception) {
            message = exception.getMessage();
            return null;
        }
    }
}
