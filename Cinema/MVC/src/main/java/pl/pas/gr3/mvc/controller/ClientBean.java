package pl.pas.gr3.mvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.dto.users.ClientInputDTO;
import pl.pas.gr3.mvc.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class ClientBean implements Serializable {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final String restBaseURL = "http://localhost:8000/api/v1";

    private String clientsBaseURL;

    private User user = new User();
    private List<ClientDTO> listOfClientDTOs = new ArrayList<>();
    private List<TicketDTO> listOfTicketDTOs = new ArrayList<>();
    private int operationStatusCode = 0;
    private String clientLogin;
    private String message = "";

    @PostConstruct
    private void initializeData() {
        clientsBaseURL = restBaseURL + "/clients";

        RestAssured.baseURI = clientsBaseURL;
        String path = clientsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        Response response = requestSpecification.get(path);
    }

    // Create methods

    public void createClient() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        List<String> messages = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (messages.isEmpty()) {
            ClientInputDTO clientInputDTO = new ClientInputDTO(user.getClientLogin(), user.getClientPassword());
            String path = clientsBaseURL;

            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String jsonPayload = objectMapper.writeValueAsString(clientInputDTO);

                RequestSpecification requestSpecification = RestAssured.given();
                requestSpecification.contentType(ContentType.JSON);
                requestSpecification.body(jsonPayload);

                Response response = requestSpecification.post(path);

                operationStatusCode = response.statusCode();

                if (response.statusCode() == 409) {
                    message = "Klient z podanym loginem już istnieje.";
                } else if (response.statusCode() == 400) {
                    message = "Wprowadzono nieprawidłowe dane";
                } else {
                    message = "Utworzono użytkownika";
                }
            } catch (JsonProcessingException exception) {
                message = "JsonProcessingException";
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String errorMessage : messages) {
                stringBuilder.append(errorMessage).append(";");
            }
            message = stringBuilder.toString();
        }
    }

    // Read methods

    public List<ClientDTO> findAllClients() {
        String path = clientsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        message = response.body().toString();

        listOfClientDTOs = new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
        return listOfClientDTOs;
    }

    public void findAllClientsMatchingLogin() {
        String path = clientsBaseURL + "?match=" + clientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        message = response.body().toString();

        listOfClientDTOs = new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
    }

    // Update methods

    public void activateClient(ClientDTO clientDTO) {
        String path = clientsBaseURL + "/" + clientDTO.getClientID() + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);

        this.findAllClients();
    }

    public void deactivateClient(ClientDTO clientDTO) {
        String path = clientsBaseURL + "/" + clientDTO.getClientID() + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);

        this.findAllClients();
    }
}
