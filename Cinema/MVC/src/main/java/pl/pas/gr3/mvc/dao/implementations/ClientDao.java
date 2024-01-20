package pl.pas.gr3.mvc.dao.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pl.pas.gr3.dto.output.ClientDTO;
import pl.pas.gr3.dto.input.ClientInputDTO;
import pl.pas.gr3.dto.update.ClientPasswordDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.dao.interfaces.IClientDao;
import pl.pas.gr3.mvc.exceptions.daos.client.*;

import java.util.ArrayList;
import java.util.List;

public class ClientDao implements IClientDao {

    @Override
    public ClientDTO create(ClientInputDTO clientInputDTO) throws ClientDaoCreateException {
        String path = GeneralConstants.CLIENTS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        ClientDTO createdClient;
        try {
            String jsonPayload = objectMapper.writeValueAsString(clientInputDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                createdClient = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);
            } else if (response.statusCode() == 409) {
                throw new ClientDaoCreateException("Użytkownik o podanym loginie już istnieje.");
            } else {
                throw new ClientDaoCreateException("Podano nieprawidłowe dane.");
            }
        } catch (JsonProcessingException exception) {
            throw new ClientDaoCreateException("W trakcie tworzenia konta klienta doszło do błędu.");
        }
        return createdClient;
    }

    @Override
    public ClientDTO readClientForChange(ClientDTO clientDTO) throws ClientDaoReadException {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID();
        ObjectMapper objectMapper = new ObjectMapper();
        ClientDTO selectedClient;
        try {
            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.accept(ContentType.JSON);

            Response response = requestSpecification.get(path);

            if (response.statusCode() == 200) {
                selectedClient = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);
            } else if (response.statusCode() == 404) {
                throw new ClientDaoReadException("Nie znaleziono konta wybranego klienta.");
            } else {
                throw new ClientDaoReadException("W trakcie odczytu konta klienta doszło do błędu.");
            }
        } catch (JsonProcessingException exception) {
            throw new ClientDaoReadException("W trakcie odczytu konta klienta doszło do błędu. Powód: " + exception.getMessage());
        }
        return selectedClient;
    }

    @Override
    public List<ClientDTO> findAll() throws ClientDaoReadException {
        List<ClientDTO> listOfClientDTOs;

        String path = GeneralConstants.CLIENTS_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        if (response.statusCode() == 200) {
            listOfClientDTOs = new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
        } else if (response.statusCode() == 404) {
            throw new ClientDaoReadException("Nie znaleziono żadnych klientów");
        } else {
            throw new ClientDaoReadException("Wystąpił błąd podczas pobierania klientów");
        }
        return listOfClientDTOs;
    }

    @Override
    public void updateClient(ClientPasswordDTO clientPasswordDTO) throws ClientDaoUpdateException {
        String path = GeneralConstants.CLIENTS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(clientPasswordDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(path);

            if (response.statusCode() == 400) {
                throw new ClientDaoUpdateException("Podano nieprawidłowe dane");
            }
        } catch (JsonProcessingException exception) {
            throw new ClientDaoUpdateException("Doszło do błędu podczas aktualizowania konta klienta. Powód: " + exception.getMessage());
        }
    }

    @Override
    public void activateClient(ClientDTO clientDTO) throws ClientDaoActivateException {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID() + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);

        if (response.statusCode() == 400) {
            throw new ClientDaoActivateException("Doszło do błędu w trakcie aktywowania klienta. Powód: " + response.getBody().asString());
        }
    }

    @Override
    public void deactivateClient(ClientDTO clientDTO) throws ClientDaoDeactivateException {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID() + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);

        if (response.statusCode() == 400) {
            throw new ClientDaoDeactivateException("Doszło do błędu w trakcie deaktywowania klienta. Powód: " + response.getBody().asString());
        }
    }
}
