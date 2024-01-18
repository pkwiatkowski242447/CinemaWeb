package pl.pas.gr3.mvc.controller.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.dto.users.ClientInputDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;
import pl.pas.gr3.mvc.exceptions.clients.ClientCreateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@SessionScoped
@Named
public class ClientControllerBean implements Serializable {

    private ClientDTO createdClient;

    private String message;

    // Actions

    public void createClient(ClientInputDTO clientInputDTO) throws ClientCreateException {
        String path = GeneralConstants.CLIENTS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonPayload = objectMapper.writeValueAsString(clientInputDTO);
            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            if (response.statusCode() == 201) {
                createdClient = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);
            } else if (response.statusCode() == 409) {
                throw new ClientCreateException("Użytkownik o podanym loginie już istnieje.");
            } else {
                throw new ClientCreateException("Podano nieprawidłowe dane.");
            }

        } catch (JsonProcessingException exception) {
            throw new ClientCreateException("W trakcie tworzenia klienta doszło do błędu.");
        }
    }

    public void activateClient(ClientDTO clientDTO) {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID() + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);
    }

    public void deactivateClient(ClientDTO clientDTO) {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID() + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);
    }
}
