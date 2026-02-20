package pl.pas.gr3.cinema.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.repository.impl.MovieRepositoryImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MovieRepositoryTest {

    private static final String DATABASE_NAME = "test";

    private static MovieRepositoryImpl movieRepositoryForTests;

    private Movie movieNo1;
    private Movie movieNo2;
    private Movie movieNo3;

    @BeforeAll
    static void init() {
        movieRepositoryForTests = new MovieRepositoryImpl(DATABASE_NAME);
    }

    @BeforeEach
    void addExampleMovies() {
        try {
            movieNo1 = movieRepositoryForTests.create("ExampleTitleNo1", 10.50, 1, 30);
            movieNo2 = movieRepositoryForTests.create("ExampleTitleNo2", 23.75, 2, 45);
            movieNo3 = movieRepositoryForTests.create("ExampleTitleNo3", 40.25, 3, 60);
        } catch (Exception exception) {
            throw new RuntimeException("Could not initialize test database while adding movies to it.", exception);
        }
    }

    @AfterEach
    void removeExampleMovies() {
        try {
            List<Movie> listOfAllMovies = movieRepositoryForTests.findAll();
            listOfAllMovies.forEach(movie -> movieRepositoryForTests.delete(movie.getId()));
        } catch (Exception exception) {
            throw new RuntimeException("Could not remove all movies from the test database after movie repository tests.", exception);
        }
    }

    @AfterAll
    static void destroy() {
        movieRepositoryForTests.close();
    }

    @Test
    void movieRepositoryConstructorTest() {
        MovieRepositoryImpl testMovieRepository = new MovieRepositoryImpl(DATABASE_NAME);
        assertNotNull(testMovieRepository);
        testMovieRepository.close();
    }

    @Test
    void movieRepositoryCreateMovieTestPositive() throws MovieRepositoryException {
        Movie movie = movieRepositoryForTests.create("OtherMovieTitleNo1", 40.75, 10, 90);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryCreateMovieWithNullMovieTitleTestNegative() {
        assertThrows(MovieRepositoryCreateException.class, () -> {
           Movie movie = movieRepositoryForTests.create(null, 50.00, 10, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithEmptyMovieTitleTestNegative() {
        String movieTitle = "";
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create(movieTitle, 50.00, 10, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithMovieTitleTooLongTestNegative() {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf1";
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create(movieTitle, 50.00, 10, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithNegativeMovieBasePriceTestNegative() {
        double movieBasePrice = -10.00;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", movieBasePrice, 10, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithMovieBasePriceTooHighTestNegative() {
        double movieBasePrice = 150.00;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", movieBasePrice, 10, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieRepositoryException {
        double movieBasePrice = 0.00;
        Movie movie = movieRepositoryForTests.create("SomeMovieTitle", movieBasePrice, 10, 90);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryCreateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieRepositoryException {
        double movieBasePrice = 100.00;
        Movie movie = movieRepositoryForTests.create("SomeMovieTitle", movieBasePrice, 10, 90);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryCreateMovieWithScreeningRoomNumberNegativeTestNegative() {
        int scrRoomNumber = -1;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50, scrRoomNumber, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int scrRoomNumber = 100;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50, scrRoomNumber, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int scrRoomNumber = 0;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50, scrRoomNumber, 90);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieRepositoryException {
        int scrRoomNumber = 1;
        Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50.00, scrRoomNumber, 90);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieRepositoryException {
        int scrRoomNumber = 30;
        Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50.00, scrRoomNumber, 90);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryCreateMovieWithNumberOfAvailableSeatsNegativeTestNegative() {
        int numberOfAvailableSeats = -1;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50, 1, numberOfAvailableSeats);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int numberOfAvailableSeats = 121;
        assertThrows(MovieRepositoryCreateException.class, () -> {
            Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50, 1, numberOfAvailableSeats);
        });
    }

    @Test
    void movieRepositoryCreateMovieWithNumberOfAvailableSeatsEqualTo1TestPositive() throws MovieRepositoryException {
        int numberOfAvailableSeats = 0;
        Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50.00, 1, numberOfAvailableSeats);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieRepositoryException {
        int numberOfAvailableSeats = 120;
        Movie movie = movieRepositoryForTests.create("SomeMovieTitle", 50.00, 1, numberOfAvailableSeats);
        assertNotNull(movie);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movie.getId());
        assertNotNull(foundMovie);
        assertEquals(movie, foundMovie);
    }

    @Test
    void movieRepositoryFindMovieTestPositive() throws MovieRepositoryException {
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
    }

    @Test
    void movieRepositoryFindMovieThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeMovieTitle", 50.00, 10, 90);
        assertNotNull(movie);
        assertThrows(MovieRepositoryMovieNotFoundException.class, () -> movieRepositoryForTests.findByUUID(movie.getId()));
    }

    @Test
    void movieRepositoryFindAllMoviesTestPositive() throws MovieRepositoryException {
        List<Movie> listOfAllMovies = movieRepositoryForTests.findAll();
        assertNotNull(listOfAllMovies);
        assertFalse(listOfAllMovies.isEmpty());
        assertEquals(3, listOfAllMovies.size());
    }

    @Test
    void movieRepositoryUpdateMovieTestPositive() throws MovieRepositoryException {
        String movieTitleBefore = movieNo1.getTitle();
        String newMovieTitle = "Pulp Fiction";
        movieNo1.setTitle(newMovieTitle);
        movieRepositoryForTests.update(movieNo1);
        String movieTitleAfter = movieNo1.getTitle();
        assertNotNull(movieTitleAfter);
        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    void movieRepositoryUpdateMovieThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeMovieTitle", 50.00, 10, 90);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movie));
    }

    @Test
    void movieRepositoryUpdateMovieWithNullMovieTitleTestNegative() {
        movieNo1.setTitle(null);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithEmptyMovieTitleTestNegative() {
        String newMovieTitle = "";
        movieNo1.setTitle(newMovieTitle);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithMovieTitleTooLongTestNegative() {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf1";
        movieNo1.setTitle(newMovieTitle);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithMovieBasePriceNegativeTestNegative() {
        double newMovieBasePrice = -10.00;
        movieNo1.setBasePrice(newMovieBasePrice);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithMovieBasePriceTooHighTestNegative() {
        double newMovieBasePrice = 110;
        movieNo1.setBasePrice(newMovieBasePrice);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithMovieBasePriceEqualTo0HighTestNegative() throws MovieRepositoryException {
        double newMovieBasePrice = 0.00;
        movieNo1.setBasePrice(newMovieBasePrice);
        movieRepositoryForTests.update(movieNo1);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getBasePrice());
    }

    @Test
    void movieRepositoryUpdateMovieWithMovieBasePriceEqualTo100HighTestNegative() throws MovieRepositoryException {
        double newMovieBasePrice = 100.00;
        movieNo1.setBasePrice(newMovieBasePrice);
        movieRepositoryForTests.update(movieNo1);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getBasePrice());
    }

    @Test
    void movieRepositoryUpdateMovieWithScreeningRoomNumberNegativeTestNegative() {
        int scrRoomNumber = -1;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int scrRoomNumber = 151;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int scrRoomNumber = 0;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithScreeningRoomNumberEqualTo1HighTestNegative() throws MovieRepositoryException {
        int scrRoomNumber = 1;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        movieRepositoryForTests.update(movieNo1);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(scrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    void movieRepositoryUpdateMovieWithScreeningRoomNumberEqualTo30HighTestNegative() throws MovieRepositoryException {
        int scrRoomNumber = 30;
        movieNo1.setScrRoomNumber(scrRoomNumber);
        movieRepositoryForTests.update(movieNo1);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(scrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsNegativeTestNegative() {
        int numberOfAvailableSeats = -1;
        movieNo1.setAvailableSeats(numberOfAvailableSeats);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int numberOfAvailableSeats = 151;
        movieNo1.setAvailableSeats(numberOfAvailableSeats);
        assertThrows(MovieRepositoryUpdateException.class, () -> movieRepositoryForTests.update(movieNo1));
    }

    @Test
    void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsEqualTo0HighTestPositive() throws MovieRepositoryException {
        int numberOfAvailableSeats = 0;
        movieNo1.setAvailableSeats(numberOfAvailableSeats);
        movieRepositoryForTests.update(movieNo1);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(numberOfAvailableSeats, foundMovie.getAvailableSeats());
    }

    @Test
    void movieRepositoryUpdateMovieWithNumberOfAvailableSeatsEqualTo150HighTestNegative() throws MovieRepositoryException {
        int numberOfAvailableSeats = 30;
        movieNo1.setAvailableSeats(numberOfAvailableSeats);
        movieRepositoryForTests.update(movieNo1);
        Movie foundMovie = movieRepositoryForTests.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(numberOfAvailableSeats, foundMovie.getAvailableSeats());
    }

    @Test
    void movieRepositoryDeleteMovieTestPositive() throws MovieRepositoryException {
        int numberOfMoviesBefore = movieRepositoryForTests.findAll().size();
        UUID removedMovieUUID = movieNo1.getId();
        movieRepositoryForTests.delete(removedMovieUUID);
        int numberOfMoviesAfter = movieRepositoryForTests.findAll().size();
        assertNotEquals(numberOfMoviesBefore, numberOfMoviesAfter);
        assertEquals(3, numberOfMoviesBefore);
        assertEquals(2, numberOfMoviesAfter);
        assertThrows(MovieRepositoryReadException.class, () -> movieRepositoryForTests.findByUUID(removedMovieUUID));
    }

    @Test
    void movieRepositoryDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeMovieTitle", 50.00, 10, 90);
        assertThrows(MovieRepositoryDeleteException.class, () -> movieRepositoryForTests.delete(movie.getId()));
    }
}