package pl.pas.gr3.mvc.controller;

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
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class ClientBean implements Serializable {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger logger = LoggerFactory.getLogger(ClientBean.class);

    private List<ClientDTO> listOfClientDTOs = new ArrayList<>();

    private int operationStatusCode;
    private String message;

    @PostConstruct
    private void initializeData() {
        this.findAllClients();
    }

    // Read methods

    public void findAllClients() {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        operationStatusCode = response.statusCode();

        if (response.statusCode() == 200) {
            listOfClientDTOs = new ArrayList<>(response.jsonPath().getList(".", ClientDTO.class));
        } else if (response.statusCode() == 404) {
            message = "Nie znaleziono żadnych klientów";
        } else {
            message = response.getBody().asString();
        }
    }

    // Update methods

    public void activateClient(ClientDTO clientDTO) {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID() + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);

        this.findAllClients();
    }

    public void deactivateClient(ClientDTO clientDTO) {
        String path = GeneralConstants.CLIENTS_BASE_URL + "/" + clientDTO.getClientID() + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.body(ContentType.JSON);

        Response response = requestSpecification.post(path);

        this.findAllClients();
    }
}
