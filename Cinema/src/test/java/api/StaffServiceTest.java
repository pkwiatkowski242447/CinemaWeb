package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StaffServiceTest {

    private static String baseURL;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/staffs";
        RestAssured.baseURI = baseURL;
    }

    // Create tests
    
    @Test
    public void staffServiceCreateStaffTestPositive() {

    }

    @Test
    public void staffServiceCreateStaffWithNullLoginThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithEmptyLoginThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithLoginTooShortThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithLoginTooLongThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithLoginLengthEqualTo8ThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithLoginLengthEqualTo20ThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithLoginThatDoesNotMeetRegExTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithLoginThatIsAlreadyInTheDatabaseTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithNullPasswordThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithEmptyPasswordThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithPasswordTooShortThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithPasswordTooLongThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithPasswordLengthEqualTo8ThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithPasswordLengthEqualTo20ThatTestNegative() {

    }

    @Test
    public void staffServiceCreateStaffWithPasswordThatDoesNotMeetRegExTestNegative() {

    }

    // Read tests

    @Test
    public void staffServiceFindStaffByIDTestPositive() {

    }

    @Test
    public void staffServiceFindStaffByIDThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void staffServiceFindStaffByLoginTestPositive() {

    }

    @Test
    public void staffServiceFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void staffServiceFindFindStaffsMatchingLoginTestPositive() {

    }

    @Test
    public void staffServiceFindFindStaffsTestPositive() {

    }

    // Update tests

    @Test
    public void staffServiceUpdateStaffTestPositive() {

    }

    @Test
    public void staffServiceUpdateStaffWithNullLoginTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithEmptyLoginTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithLoginTooShortTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithLoginTooLongTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithLoginLengthEqualTo8TestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithLoginLengthEqualTo40TestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithLoginThatViolatesRegExTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithNullPasswordTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithEmptyPasswordTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithPasswordTooShortTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithPasswordTooLongTestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithPasswordLengthEqualTo8TestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithPasswordLengthEqualTo40TestNegative() {

    }

    @Test
    public void staffServiceUpdateStaffWithPasswordThatViolatesRegExTestNegative() {

    }

    // Delete tests

    @Test
    public void staffServiceDeleteStaffTestPositive() {

    }

    @Test
    public void staffServiceDeleteStaffThatIsNotInTheDatabaseTestNegative() {

    }

    // Activate tests

    @Test
    public void staffServiceActivateStaffTestPositive() {

    }

    @Test
    public void staffServiceActivateStaffThatIsNotInTheDatabaseTestNegative() {

    }

    // Deactivate tests

    @Test
    public void staffServiceDeactivateStaffTestPositive() {

    }

    @Test
    public void staffServiceDeactivateStaffThatIsNotInTheDatabaseTestNegative() {

    }
}
