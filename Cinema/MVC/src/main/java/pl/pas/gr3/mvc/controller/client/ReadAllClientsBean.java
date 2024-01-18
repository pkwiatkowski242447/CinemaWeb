package pl.pas.gr3.mvc.controller.client;

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
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.constants.GeneralConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ConversationScoped
@Named
public class ReadAllClientsBean implements Serializable {

    private List<ClientDTO> listOfClients;
    private String message;
    private int operationStatusCode;

    @Inject
    private ClientControllerBean clientControllerBean;

    @PostConstruct
    public void beanInit() {
        listOfClients = this.findAllClients();
    }

    public List<ClientDTO> findAllClients() {
        List<ClientDTO> listOfClientDTOs = new ArrayList<>();

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
            message = "Wystąpił błąd podczas pobierania klientów";
        }
        return listOfClientDTOs;
    }

    public String activateClient(ClientDTO clientDTO) {
        clientControllerBean.activateClient(clientDTO);
        return "listOfAllClients";
    }

    public String deactivateClient(ClientDTO clientDTO) {
        clientControllerBean.deactivateClient(clientDTO);
        return "listOfAllClients";
    }
}
