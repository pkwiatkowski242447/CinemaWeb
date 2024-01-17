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
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.dto.users.ClientInputDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class CreateClientBean implements Serializable {

    private String clientLogin;
    private String clientPasswordFirst;
    private String clientPasswordSecond;

    private ClientDTO createdClient;

    private String message;
    private int operationStatus;

    // Create methods

    public String createClient() {
        String path = GeneralConstants.CLIENTS_BASE_URL;
        ObjectMapper objectMapper = new ObjectMapper();

        if (clientPasswordFirst.equals(clientPasswordSecond)) {
            try {
                String jsonPayload = objectMapper.writeValueAsString(new ClientInputDTO(clientLogin, clientPasswordFirst));
                RequestSpecification requestSpecification = RestAssured.given();
                requestSpecification.contentType(ContentType.JSON);
                requestSpecification.body(jsonPayload);

                Response response = requestSpecification.post(path);

                if (response.statusCode() == 201) {
                    createdClient = objectMapper.readValue(response.getBody().asString(), ClientDTO.class);
                    return "clientCreatedSuccessfully";
                } else if (response.statusCode() == 409) {
                    message = "Użytkownik o podanym loginie już istnieje.";
                    return null;
                } else {
                    message = response.getBody().asString();
                    return null;
                }

            } catch (JsonProcessingException exception) {
                message = exception.getMessage();
                return null;
            }
        } else {
            message = "Podane hasła nie są zgodne";
            return null;
        }
    }
}
