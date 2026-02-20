package pl.pas.gr3.cinema.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.entity.Movie;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    private static UUID uuidNo1;
    private static UUID uuidNo2;
    private static String movieTitleNo1;
    private static String movieTitleNo2;
    private static double movieBasePriceNo1;
    private static double movieBasePriceNo2;
    private static int scrRoomNumberNo1;
    private static int scrRoomNumberNo2;
    private static int numOfAvailableSeatsNo1;
    private static int numOfAvailableSeatsNo2;

    private Movie movieNo1;
    private Movie movieNo2;
    private Movie movieNo3;

    @BeforeAll
    static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        movieTitleNo1 = "Cars";
        movieTitleNo2 = "Joker";
        movieBasePriceNo1 = 40.50;
        movieBasePriceNo2 = 35.75;
        scrRoomNumberNo1 = 1;
        scrRoomNumberNo2 = 2;
        numOfAvailableSeatsNo1 = 45;
        numOfAvailableSeatsNo2 = 60;
    }

    @BeforeEach
    void initializeMovieObjects() {
        movieNo1 = new Movie(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numOfAvailableSeatsNo1);
        movieNo2 = new Movie(uuidNo2, movieTitleNo2, movieBasePriceNo2, scrRoomNumberNo2, numOfAvailableSeatsNo2);
        movieNo3 = new Movie(movieNo1.getId(),
                movieNo1.getTitle(),
                movieNo1.getBasePrice(),
                movieNo1.getScrRoomNumber(),
                movieNo1.getAvailableSeats());
    }

    @Test
    void movieConstructorAndGettersTest() {
        Movie testMovie =  new Movie(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numOfAvailableSeatsNo1);
        assertNotNull(testMovie);
        assertEquals(uuidNo1, testMovie.getId());
        assertEquals(movieTitleNo1, testMovie.getTitle());
        assertEquals(movieBasePriceNo1, testMovie.getBasePrice());
        assertEquals(scrRoomNumberNo1, testMovie.getScrRoomNumber());
        assertEquals(numOfAvailableSeatsNo1, testMovie.getAvailableSeats());
    }

    @Test
    void movieTitleSetterTest() {
        String movieTitleBefore = movieNo1.getTitle();
        String newMovieTitle = "Other Title";
        movieNo1.setTitle(newMovieTitle);
        String movieTitleAfter = movieNo1.getTitle();
        assertNotNull(movieTitleAfter);
        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    void movieBasePriceSetterTest() {
        double movieBasePriceBefore = movieNo1.getBasePrice();
        double newMovieBasePrice = 50.00;
        movieNo1.setBasePrice(newMovieBasePrice);
        double movieBasePriceAfter = movieNo1.getBasePrice();
        assertNotEquals(0, movieBasePriceAfter);
        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
    }

    @Test
    void movieScrRoomSetterTest() {
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int newScrRoomNumber = 10;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        int scrRoomNumberAfter = movieNo1.getScrRoomNumber();
        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
    }

    @Test
    void movieNumberOfAvailableSeatsSetterTest() {
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();
        int newNumberOfAvailableSeats = 10;
        movieNo1.setAvailableSeats(newNumberOfAvailableSeats);
        int numberOfAvailableSeatsAfter = movieNo1.getAvailableSeats();
        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }

    @Test
    void movieEqualsMethodWithItself() {
        boolean equalsResult = movieNo1.equals(movieNo1);
        assertTrue(equalsResult);
    }

    @Test
    void movieEqualsMethodWithNull() {
        boolean equalsResult = movieNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    void movieEqualsMethodWithObjectOfDifferentClass() {
        boolean equalsResult = movieNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    void movieEqualsMethodWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = movieNo1.equals(movieNo2);
        assertFalse(equalsResult);
    }

    @Test
    void movieEqualsMethodWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = movieNo1.equals(movieNo3);
        assertTrue(equalsResult);
    }

    @Test
    void movieHashCodeTestPositive() {
        int hashCodeFromMovieNo1 = movieNo1.hashCode();
        int hashCodeFromMovieNo3 = movieNo3.hashCode();
        assertEquals(hashCodeFromMovieNo1, hashCodeFromMovieNo3);
        assertEquals(movieNo1, movieNo3);
    }

    @Test
    void movieHashCodeTestNegative() {
        int hashCodeFromMovieNo1 = movieNo1.hashCode();
        int hashCodeFromMovieNo2 = movieNo2.hashCode();
        assertNotEquals(hashCodeFromMovieNo1, hashCodeFromMovieNo2);
    }

    @Test
    void movieToStringMethodTest() {
        String resultString = movieNo1.toString();
        assertNotNull(resultString);
        assertFalse(resultString.isEmpty());
    }
}