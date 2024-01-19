package pl.pas.gr3.mvc.dao.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.dao.interfaces.ITicketDao;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketCreateException;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketDeleteException;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketReadException;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketUpdateException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoCreateException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoDeleteException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoUpdateException;

import java.util.ArrayList;
import java.util.List;

public class TicketDao implements ITicketDao {
    @Override
    public TicketWithUserAndMovie create(TicketInputDTO ticketInputDTO) throws TicketDaoCreateException {
        String path = GeneralConstants.TICKETS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TicketWithUserAndMovie createdTicket;
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
                throw new TicketDaoCreateException("Wprowadzono błędne dane");
            }

        } catch (JsonProcessingException exception) {
            throw new TicketDaoCreateException("W trakcie tworzenia biletu doszło do błędu");
        }
        return createdTicket;
    }

    @Override
    public TicketWithUserAndMovie readTicketForChanges(TicketDTO ticketDTO) throws TicketDaoReadException {
        String path = GeneralConstants.TICKETS_BASE_URL + "/" + ticketDTO.getTicketID();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TicketWithUserAndMovie selectedTicket;
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
                throw new TicketDaoReadException("Nie znaleziono wybranego biletu.");
            } else {
                throw new TicketDaoReadException("Nastąpił błąd podczas znajdowania wybranego biletu.");
            }
        } catch (JsonProcessingException exception) {
            throw new TicketDaoReadException(exception.getMessage(), exception);
        }
        return selectedTicket;
    }

    @Override
    public List<TicketWithUserAndMovie> findAll() throws TicketDaoReadException {
        List<TicketDTO> listOfTicketDTOs;
        List<TicketWithUserAndMovie> listOfFoundTickets = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String path = GeneralConstants.TICKETS_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

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
                throw new TicketDaoReadException(exception.getMessage());
            }
        }
        return listOfFoundTickets;
    }

    @Override
    public void updateTicket(TicketDTO ticketDTO) throws TicketDaoUpdateException {
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
                throw new TicketDaoUpdateException("Podano nieprawidłowe dane");
            }
        } catch (JsonProcessingException exception) {
            throw new TicketDaoUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deleteTicket(TicketDTO ticketDTO) throws TicketDaoDeleteException {
        String path = GeneralConstants.TICKETS_BASE_URL + "/" + ticketDTO.getTicketID();

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);

        if (response.statusCode() == 400) {
            throw new TicketDaoDeleteException("Wystąpił błąd podczas usuwania biletu.");
        }
    }
}
