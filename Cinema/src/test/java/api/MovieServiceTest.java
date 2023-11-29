package api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MovieServiceTest {

    private static String baseURL;

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/admins";
        RestAssured.baseURI = baseURL;
    }

    // Create tests

    @Test
    public void movieServiceCreateMovieTestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithNullMovieTitleTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithEmptyMovieTitleTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleTooShortTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleTooLongTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleLengthEqualTo1TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieTitleLengthEqualTo150TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithNegativeMovieBasePriceTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieBasePriceTooHighTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieBasePriceEqualTo0TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithMovieBasePriceEqualTo100TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberTooHighTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {

    }

    @Test
    public void movieServiceCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {

    }

    @Test
    public void movieServiceCreateMovieWithNumberOfAvailableSeatsEqualTo150TestPositive() {

    }

    // Read tests

    @Test
    public void movieServiceFindMovieByIDTestPositive() {

    }

    @Test
    public void movieServiceFindMovieByIDThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void movieServiceFindMovieByLoginTestPositive() {

    }

    @Test
    public void movieServiceFindMovieByLoginThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void movieServiceFindAllMoviesMatchingLoginTestPositive() {

    }

    @Test
    public void movieServiceFindAllMoviesTestPositive() {

    }

    // Update tests

    @Test
    public void movieServiceUpdateMovieTestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithNullMovieTitleTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleTooShortTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleTooLongTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleLengthEqualTo1TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithEmptyMovieTitleLengthEqualTo150TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithNegativeMovieBasePriceTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithMovieBasePriceTooHighTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithMovieBasePriceEqualTo0TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithMovieBasePriceEqualTo150TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {

    }

    @Test
    public void movieServiceUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {

    }

    @Test
    public void movieServiceUpdateMovieWithNumberOfAvailableSeatsEqualTo150TestPositive() {

    }

    // Delete tests

    @Test
    public void movieServiceDeleteMovieTestPositive() {

    }

    @Test
    public void movieServiceDeleteMovieThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void movieServiceDeleteMovieThatIsUsedInTicketTestNegative() {

    }
}
