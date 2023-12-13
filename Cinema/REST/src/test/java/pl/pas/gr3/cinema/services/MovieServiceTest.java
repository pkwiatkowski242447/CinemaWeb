package pl.pas.gr3.cinema.services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.services.crud.movie.*;
import pl.pas.gr3.cinema.services.implementations.MovieService;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private static final String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(MovieServiceTest.class);

    private static MovieRepository movieRepository;
    private static MovieService movieService;

    private Movie movieNo1;
    private Movie movieNo2;

    @BeforeAll
    public static void initialize() {
        movieRepository = new MovieRepository(databaseName);
        movieService = new MovieService(movieRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            movieNo1 = movieService.create("UniqueMovieTitleNo1", 45.00, 1, 60);
            movieNo2 = movieService.create("UniqueMovieTitleNo2", 35.50, 2, 40);
        } catch (MovieServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        try {
            List<Movie> listOfMovies = movieService.findAll();
            for (Movie movie : listOfMovies) {
                movieService.delete(movie.getMovieID());
            }
        } catch (MovieServiceReadException | MovieServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        movieRepository.close();
    }

    // Create tests

    @Test
    public void movieManagerCreateMovieTestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithNullMovieTitleTestNegative() {
        String movieTitle = null;
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithEmptyMovieTitleTestNegative() {
        String movieTitle = "";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleTooShortTestNegative() {
        String movieTitle = "";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleTooLongTestNegative() {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleLengthEqualTo1TestPositive() throws MovieServiceCreateException {
        String movieTitle = "d";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithMovieTitleLengthEqualTo150TestPositive() throws MovieServiceCreateException {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithNegativeMovieBasePriceTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = -1.00;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithMovieBasePriceTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 101.50;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 0;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 0;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 31;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 1;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = -1;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 121;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    public void movieManagerCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 0;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 120;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getMovieTitle());
        assertEquals(movieBasePrice, movie.getMovieBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getNumberOfAvailableSeats());
    }

    // Read tests

    @Test
    public void movieManagerFindMovieByIDTestPositive() throws MovieServiceReadException {
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
    }

    @Test
    public void movieManagerFindMovieByIDThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeExampleTitleNo1", 25.75, 4, 40);
        assertNotNull(movie);
        assertThrows(MovieServiceMovieNotFoundException.class, () -> movieService.findByUUID(movie.getMovieID()));
    }

    @Test
    public void movieManagerFindAllMoviesTestPositive() throws MovieServiceReadException {
        List<Movie> listOfMovies = movieService.findAll();
        assertNotNull(listOfMovies);
        assertFalse(listOfMovies.isEmpty());
        assertEquals(2, listOfMovies.size());
    }

    // Update tests

    @Test
    public void movieManagerUpdateMovieTestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        String movieTitleBefore = movieNo1.getMovieTitle();
        double movieBasePriceBefore = movieNo1.getMovieBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getNumberOfAvailableSeats();

        String newMovieTitle = "SomeNewMovieTitleNo1";
        double newMovieBasePrice = 50.25;
        int newScrRoomNumber = 19;
        int newNumberOfAvailableSeats = 27;

        movieNo1.setMovieTitle(newMovieTitle);
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);

        movieService.update(movieNo1);

        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());

        String movieTitleAfter = foundMovie.getMovieTitle();
        double movieBasePriceAfter = foundMovie.getMovieBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableAfter = foundMovie.getNumberOfAvailableSeats();

        assertEquals(newMovieTitle, movieTitleAfter);
        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertEquals(newNumberOfAvailableSeats, numberOfAvailableAfter);

        assertNotEquals(movieTitleBefore, movieTitleAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableAfter);
    }

    @Test
    public void movieManagerUpdateMovieWithNullMovieTitleTestNegative() {
        String newMovieTitle = null;
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleTestNegative() {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleTooShortTestNegative() {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleTooLongTestNegative() {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        movieNo1.setMovieTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleLengthEqualTo1TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        String newMovieTitle = "d";
        movieNo1.setMovieTitle(newMovieTitle);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieTitle, foundMovie.getMovieTitle());
    }

    @Test
    public void movieManagerUpdateMovieWithEmptyMovieTitleLengthEqualTo150TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        movieNo1.setMovieTitle(newMovieTitle);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieTitle, foundMovie.getMovieTitle());
    }

    @Test
    public void movieManagerUpdateMovieWithNegativeMovieBasePriceTestNegative() {
        double newMovieBasePrice = -1.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithMovieBasePriceTooHighTestNegative() {
        double newMovieBasePrice = 101.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        double newMovieBasePrice = 0;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getMovieBasePrice());
    }

    @Test
    public void movieManagerUpdateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        double newMovieBasePrice = 100;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getMovieBasePrice());
    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int newScrRoomNumber = 0;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int newScrRoomNumber = 31;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieServiceReadException {
        int newScrRoomNumber = 1;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newScrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    public void movieManagerUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        int newScrRoomNumber = 30;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newScrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    public void movieManagerUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        int newNumberOfAvailableSeats = -1;
        movieNo1.setScrRoomNumber(newNumberOfAvailableSeats);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int newNumberOfAvailableSeats = 121;
        movieNo1.setScrRoomNumber(newNumberOfAvailableSeats);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    public void movieManagerUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        int newNumberOfAvailableSeats = 0;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newNumberOfAvailableSeats, foundMovie.getNumberOfAvailableSeats());
    }

    @Test
    public void movieManagerUpdateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        int newNumberOfAvailableSeats = 120;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getMovieID());
        assertNotNull(foundMovie);
        assertEquals(newNumberOfAvailableSeats, foundMovie.getNumberOfAvailableSeats());
    }

    // Delete tests

    @Test
    public void movieManagerDeleteMovieTestPositive() throws MovieServiceReadException, MovieServiceDeleteException {
        UUID removedMovieUUID = movieNo1.getMovieID();
        Movie foundMovie = movieService.findByUUID(removedMovieUUID);
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
        movieService.delete(removedMovieUUID);
        assertThrows(MovieServiceReadException.class, () -> movieService.findByUUID(removedMovieUUID));
    }

    @Test
    public void movieManagerDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeExampleTitleNo1", 25.75, 4, 40);
        assertNotNull(movie);
        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(movie.getMovieID()));
    }
}
