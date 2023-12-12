package pl.pas.gr3.mvc.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.dto.users.ClientInputDTO;
import pl.pas.gr3.mvc.model.Client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApplicationScoped
@Named
public class ClientBean implements Serializable {

    private final String clientsBaseURL = "http://localhost:8000/api/v1/clients";
    private final Client client = new Client();
    private List<ClientDTO> listOfDTOs = new ArrayList<>();
    private int operationStatusCode = 0;
    private String clientLogin;
    private String message;

    @PostConstruct
    private void initializeData() {
        RestAssured.baseURI = clientsBaseURL;
        String path = clientsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        Response response = requestSpecification.get(path);
    }

    public void createClient() {
        ClientInputDTO clientInputDTO = new ClientInputDTO(client.getClientLogin(), client.getClientPassword());
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String path = clientsBaseURL;

            String jsonPayload = jsonb.toJson(clientInputDTO);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(path);

            operationStatusCode = response.statusCode();
        } catch (Exception exception) {
            operationStatusCode = -1;
        }
    }

    public List<ClientDTO> findAllClients() {
        String path = clientsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        message = response.body().toString();

        return new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
    }

    public void findAllClientsMatchingLogin() {
        String path = clientsBaseURL + "?match=" + clientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        message = response.body().toString();

        listOfDTOs = new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
    }
}
