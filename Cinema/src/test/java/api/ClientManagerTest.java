package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.ClientTest;
import org.eclipse.jetty.util.ajax.JSON;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.dto.users.ClientTestDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class ClientManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(ClientManagerTest.class);
    private static String baseURL;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/clients";
        RestAssured.baseURI = baseURL;
    }

    // Create tests

    @Test
    public void clientManagerCreateClientTestPositive() {
        ClientTestDTO clientTestDTO = new ClientTestDTO("SecretLoginNo1", "SecretPasswordNo1");
        String jsonString = JSON.toString(clientTestDTO);
        given()
                .contentType(ContentType.JSON)
                .body(jsonString)
                .when()
                .accept(ContentType.JSON)
                .post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    public void clientManagerGetClientByLoginTestPositive() {
        given()
                .pathParam("login", "SomeLogin")
                .when()
                .accept(MediaType.APPLICATION_JSON)
                .get("/login/{login}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("client-id", equalTo("8290f2fe-473e-4f27-b093-e2d5a2c8b2ce"))
                .body("client-login", equalTo("SomeLogin"))
                .body("client-status-active", equalTo(true));
    }

    @Test
    public void clientManagerGetClientByLoginTestNegative() {
        String searchedLogin = "SomeNonExistentLogin";
        given()
                .pathParam("login", searchedLogin)
                .when()
                .accept(MediaType.APPLICATION_JSON)
                .get("/login/{login}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void clientManagerGetClientByIDTestPositive() {
        String searchedID = "8290f2fe-473e-4f27-b093-e2d5a2c8b2ce";
        given()
                .pathParam("id", searchedID)
                .when()
                .accept(MediaType.APPLICATION_JSON)
                .get("/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("client-id", equalTo("8290f2fe-473e-4f27-b093-e2d5a2c8b2ce"))
                .body("client-name", equalTo("SomeLogin"))
                .body("client-status-active", equalTo(true));
    }

    @Test
    public void clientManagerGetClientByIDTestNegative() {
        String searchedID = "9290f2fe-473e-4f27-b093-e2d5a2c8b2ce";
        given()
                .pathParam("id", searchedID)
                .when()
                .accept(MediaType.APPLICATION_JSON)
                .get("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
