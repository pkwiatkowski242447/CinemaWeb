package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdminServiceTest {

    private static String baseURL;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/admins";
        RestAssured.baseURI = baseURL;
    }

    // Create tests

    @Test
    public void adminServiceCreateAdminTestPositive() {

    }

    @Test
    public void adminServiceCreateAdminWithNullLoginThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithEmptyLoginThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithLoginTooShortThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithLoginTooLongThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithLoginLengthEqualTo8ThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithLoginLengthEqualTo20ThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithLoginThatIsAlreadyInTheDatabaseTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithNullPasswordThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithEmptyPasswordThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithPasswordTooShortThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithPasswordTooLongThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithPasswordLengthEqualTo8ThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithPasswordLengthEqualTo20ThatTestNegative() {

    }

    @Test
    public void adminServiceCreateAdminWithPasswordThatDoesNotMeetRegExTestNegative() {

    }

    // Read tests

    @Test
    public void adminServiceFindAdminByIDTestPositive() {

    }

    @Test
    public void adminServiceFindAdminByIDThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void adminServiceFindAdminByLoginTestPositive() {

    }

    @Test
    public void adminServiceFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void adminServiceFindAllAdminsMatchingLoginTestPositive() {

    }

    @Test
    public void adminServiceFindAllAdminTestPositive() {

    }

    // Update tests

    @Test
    public void adminServiceUpdateAdminTestPositive() {

    }

    @Test
    public void adminServiceUpdateAdminWithNullLoginTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithEmptyLoginTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithLoginTooShortTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithLoginTooLongTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithLoginLengthEqualTo8TestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithLoginLengthEqualTo40TestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithLoginThatViolatesRegExTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithNullPasswordTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithEmptyPasswordTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithPasswordTooShortTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithPasswordTooLongTestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithPasswordLengthEqualTo8TestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithPasswordLengthEqualTo40TestNegative() {

    }

    @Test
    public void adminServiceUpdateAdminWithPasswordThatViolatesRegExTestNegative() {

    }

    // Delete tests

    @Test
    public void adminServiceDeleteAdminTestPositive() {

    }

    @Test
    public void adminServiceDeleteAdminThatIsNotInTheDatabaseTestNegative() {

    }

    // Activate tests

    @Test
    public void adminServiceActivateAdminTestPositive() {

    }

    @Test
    public void adminServiceActivateAdminThatIsNotInTheDatabaseTestNegative() {

    }

    // Deactivate tests

    @Test
    public void adminServiceDeactivateAdminTestPositive() {

    }

    @Test
    public void adminServiceDeactivateAdminThatIsNotInTheDatabaseTestNegative() {

    }
}
