package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TicketServiceTest {

    private static String baseURL;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/admins";
        RestAssured.baseURI = baseURL;
    }

    // Create tests

    @Test
    public void ticketServiceCreateTicketTestPositive() {

    }

    @Test
    public void ticketServiceCreateTicketWithNegativeTicketFinalPriceTestNegative() {

    }

    @Test
    public void ticketServiceCreateTicketWithNullClientTestNegative() {

    }

    @Test
    public void ticketServiceCreateTicketWithNullMovieTestNegative() {

    }

    // Read tests

    @Test
    public void ticketServiceFindTicketByIDTestPositive() {

    }

    @Test
    public void ticketServiceFindTicketByIDThatIsNotInTheDatabaseTestPositive() {

    }

    @Test
    public void ticketServiceFindAllTicketsTestPositive() {

    }

    // Update tests

    @Test
    public void ticketServiceUpdateTicketTestPositive() {

    }

    @Test
    public void ticketServiceUpdateTicketThatIsNotInTheDatabaseTestNegative() {

    }

    // Delete tests

    @Test
    public void ticketServiceDeleteTicketTestPositive() {

    }

    @Test
    public void ticketServiceDeleteTicketThatIsNotInTheDatabaseTestPositive() {

    }
}
