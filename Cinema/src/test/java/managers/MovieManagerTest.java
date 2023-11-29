package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieManagerTest {

    @BeforeAll
    public static void initialize() {

    }

    @BeforeEach
    public void initializeSampleData() {

    }

    @AfterEach
    public void destroySampleData() {

    }

    // Create tests

    @Test
    public void movieManagerCreateMovieTestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithNullMovieTitleTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithEmptyMovieTitleTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleTooShortTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleTooLongTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleLengthEqualTo1TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleLengthEqualTo150TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithNegativeMovieBasePriceTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieBasePriceTooHighTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieBasePriceEqualTo0TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithMovieBasePriceEqualTo100TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberTooHighTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {

    }

    @Test
    public void movieManagerCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {

    }

    @Test
    public void movieManagerCreateMovieWithNumberOfAvailableSeatsEqualTo150TestPositive() {

    }

    // Read tests

    @Test
    public void movieManagerFindMovieByIDTestPositive() {

    }

    @Test
    public void movieManagerFindMovieByIDThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void movieManagerFindMovieByLoginTestPositive() {

    }

    @Test
    public void movieManagerFindMovieByLoginThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void movieManagerFindAllMoviesMatchingLoginTestPositive() {

    }

    @Test
    public void movieManagerFindAllMoviesTestPositive() {

    }

    // Update tests

    @Test
    public void movieManagerUpdateMovieTestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithNullMovieTitleTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleTooShortTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleTooLongTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleLengthEqualTo1TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleLengthEqualTo150TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithNegativeMovieBasePriceTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithMovieBasePriceTooHighTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithMovieBasePriceEqualTo0TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithMovieBasePriceEqualTo150TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {

    }

    @Test
    public void movieManagerUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {

    }

    @Test
    public void movieManagerUpdateMovieWithNumberOfAvailableSeatsEqualTo150TestPositive() {

    }

    // Delete tests

    @Test
    public void movieManagerDeleteMovieTestPositive() {

    }

    @Test
    public void movieManagerDeleteMovieThatIsNotInTheDatabaseTestNegative() {

    }

    @Test
    public void movieManagerDeleteMovieThatIsUsedInTicketTestNegative() {

    }
}
