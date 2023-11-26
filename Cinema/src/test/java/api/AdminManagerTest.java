package api;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdminManagerTest {

    private static String baseURL;
    private static Client client;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/admins";
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void destroy() {
        client.close();
    }

    // Create tests

    @Test
    public void adminManagerCreateAdminTestPositive() {
        WebTarget webTarget = client.target(baseURL).path("create").queryParam("login", "secretLoginNo1").queryParam("password", "secretPasswordNo1");
        webTarget.request(MediaType.APPLICATION_JSON);
    }

    @Test
    public void adminManagerCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {

    }

    @Test
    public void adminManagerCreateAdminWithLoginThatIsAlreadyInTheDatabaseTestNegative() {

    }

    // Read tests

    @Test
    public void adminManagerFindAdminByIDTestPositive() {

    }

    @Test
    public void adminManagerFindAllAdminTestPositive() {

    }

    // Update tests

    @Test
    public void adminManagerUpdateAdminTestPositive() {

    }

    @Test
    public void adminManagerUpdateAdminWithEmptyPasswordTestNegative() {

    }

    // Delete tests

    @Test
    public void adminManagerDeleteAdminTestPositive() {

    }
}
