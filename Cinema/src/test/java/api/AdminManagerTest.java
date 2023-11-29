package api;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
//        AdminTestDTO adminCredentials = new AdminTestDTO("secretLoginNo1", "secretPasswordNo1");
//        WebTarget adminResource = client.target(baseURL);
//        try (Response createAdminResponse = adminResource.request(MediaType.APPLICATION_JSON).post(Entity.json(adminCredentials))) {
//            assertEquals(Response.Status.CREATED.getStatusCode(), createAdminResponse.getStatus());
//            assertEquals(createAdminResponse.getEntity(), Entity.entity(adminCredentials, MediaType.APPLICATION_JSON));
//        }
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

    @Test
    public void adminManagerFindAdminByLogin() {
        String searchedLogin = "AdminLogin";
        WebTarget adminResource = client.target(baseURL).path("/login/{login}").resolveTemplate("login", searchedLogin);
        Response response = adminResource.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
