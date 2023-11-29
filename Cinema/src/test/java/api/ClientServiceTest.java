package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClientServiceTest {

    private static String baseURL;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/clients";
        RestAssured.baseURI = baseURL;
    }

    // Create tests

    @Test
    public void clientServiceCreateClientTestPositive() {

    }

    @Test
    public void clientServiceCreateClientWithNullLoginThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithEmptyLoginThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithLoginTooShortThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithLoginTooLongThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithLoginLengthEqualTo8ThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithLoginLengthEqualTo20ThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithLoginThatDoesNotMeetRegExTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithLoginThatIsAlreadyInTheDatabaseTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithNullPasswordThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithEmptyPasswordThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithPasswordTooShortThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithPasswordTooLongThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithPasswordLengthEqualTo8ThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithPasswordLengthEqualTo20ThatTestNegative() {

    }

    @Test
    public void clientServiceCreateClientWithPasswordThatDoesNotMeetRegExTestNegative() {

    }

    // Read tests

    @Test
    public void clientServiceFindClientByIDTestPositive() {

    }

    @Test
    public void clientServiceFindClientByIDThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void clientServiceFindClientByLoginTestPositive() {

    }

    @Test
    public void clientServiceFindClientByLoginThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void clientServiceFindAllClientsMatchingLoginTestPositive() {

    }

    @Test
    public void clientServiceFindAllClientsTestPositive() {

    }

    // Update tests

    @Test
    public void clientServiceUpdateClientTestPositive() {

    }

    @Test
    public void clientServiceUpdateClientWithNullLoginTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithEmptyLoginTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithLoginTooShortTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithLoginTooLongTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithLoginLengthEqualTo8TestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithLoginLengthEqualTo40TestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithLoginThatViolatesRegExTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithNullPasswordTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithEmptyPasswordTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithPasswordTooShortTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithPasswordTooLongTestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithPasswordLengthEqualTo8TestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithPasswordLengthEqualTo40TestNegative() {

    }

    @Test
    public void clientServiceUpdateClientWithPasswordThatViolatesRegExTestNegative() {

    }

    // Delete tests

    @Test
    public void clientServiceDeleteClientTestPositive() {

    }

    @Test
    public void clientServiceDeleteClientThatIsNotInTheDatabaseTestNegative() {

    }

    // Activate tests

    @Test
    public void clientServiceActivateClientTestPositive() {

    }

    @Test
    public void clientServiceActivateClientThatIsNotInTheDatabaseTestNegative() {

    }

    // Deactivate tests

    @Test
    public void clientServiceDeactivateClientTestPositive() {

    }

    @Test
    public void clientServiceDeactivateClientThatIsNotInTheDatabaseTestNegative() {

    }
}
