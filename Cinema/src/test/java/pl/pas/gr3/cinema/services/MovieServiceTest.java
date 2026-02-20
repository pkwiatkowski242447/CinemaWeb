package pl.pas.gr3.cinema.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.service.impl.MovieServiceImpl;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.repository.impl.MovieRepositoryImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MovieServiceTest {

    private static final String DATABASE_NAME = "test";

    private static MovieRepositoryImpl movieRepository;
    private static MovieServiceImpl movieService;

    private Movie movieNo1;
    private Movie movieNo2;

    @BeforeAll
    static void initialize() {
        movieRepository = new MovieRepositoryImpl(DATABASE_NAME);
        movieService = new MovieServiceImpl(movieRepository);
    }

    @BeforeEach
    void initializeSampleData() {
        try {
            movieNo1 = movieService.create("UniqueMovieTitleNo1", 45.00, 1, 60);
            movieNo2 = movieService.create("UniqueMovieTitleNo2", 35.50, 2, 40);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterEach
    void destroySampleData() {
        try {
            List<Movie> listOfMovies = movieService.findAll();
            listOfMovies.forEach(movie -> movieService.delete(movie.getId()));
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterAll
    static void destroy() {
        movieRepository.close();
    }

    // Constructor tests

    @Test
    void movieServiceAllArgsConstructorTestPositive() {
        MovieServiceImpl testMovieService = new MovieServiceImpl(movieRepository);
        assertNotNull(testMovieService);
    }
    
    // Create tests

    @Test
    void movieServiceCreateMovieTestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithNullMovieTitleTestNegative() {
        String movieTitle = null;
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithEmptyMovieTitleTestNegative() {
        String movieTitle = "";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithMovieTitleTooShortTestNegative() {
        String movieTitle = "";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithMovieTitleTooLongTestNegative() {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithMovieTitleLengthEqualTo1TestPositive() throws MovieServiceCreateException {
        String movieTitle = "d";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithMovieTitleLengthEqualTo150TestPositive() throws MovieServiceCreateException {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithNegativeMovieBasePriceTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = -1.00;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithMovieBasePriceTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 101.50;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 0;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 0;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithScreeningRoomNumberTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 31;
        int numberOfAvailableSeats = 30;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 1;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 30;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = -1;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 42.25;
        int scrRoomNumber = 10;
        int numberOfAvailableSeats = 121;
        assertThrows(MovieServiceCreateException.class, () -> movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats));
    }

    @Test
    void movieServiceCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 0;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    @Test
    void movieServiceCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieServiceCreateException {
        String movieTitle = "SomeOtherMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 120;
        Movie movie = movieService.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
        assertNotNull(movie);
        assertEquals(movieTitle, movie.getTitle());
        assertEquals(movieBasePrice, movie.getBasePrice());
        assertEquals(scrRoomNumber, movie.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movie.getAvailableSeats());
    }

    // Read tests

    @Test
    void movieServiceFindMovieByIDTestPositive() throws MovieServiceReadException {
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
    }

    @Test
    void movieServiceFindMovieByIDThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeExampleTitleNo1", 25.75, 4, 40);
        assertNotNull(movie);
        assertThrows(MovieServiceMovieNotFoundException.class, () -> movieService.findByUUID(movie.getId()));
    }

    @Test
    void movieServiceFindAllMoviesTestPositive() throws MovieServiceReadException {
        List<Movie> listOfMovies = movieService.findAll();
        assertNotNull(listOfMovies);
        assertFalse(listOfMovies.isEmpty());
        assertEquals(2, listOfMovies.size());
    }

    // Update tests

    @Test
    void movieServiceUpdateMovieTestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeNewMovieTitleNo1";
        double newMovieBasePrice = 50.25;
        int newScrRoomNumber = 19;
        int newNumberOfAvailableSeats = 27;

        movieNo1.setTitle(newMovieTitle);
        movieNo1.setBasePrice(newMovieBasePrice);
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieNo1.setAvailableSeats(newNumberOfAvailableSeats);

        movieService.update(movieNo1);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableAfter = foundMovie.getAvailableSeats();

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
    void movieServiceUpdateMovieWithNullMovieTitleTestNegative() {
        String newMovieTitle = null;
        movieNo1.setTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithEmptyMovieTitleTestNegative() {
        String newMovieTitle = "";
        movieNo1.setTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithEmptyMovieTitleTooShortTestNegative() {
        String newMovieTitle = "";
        movieNo1.setTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithEmptyMovieTitleTooLongTestNegative() {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        movieNo1.setTitle(newMovieTitle);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithEmptyMovieTitleLengthEqualTo1TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        String newMovieTitle = "d";
        movieNo1.setTitle(newMovieTitle);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newMovieTitle, foundMovie.getTitle());
    }

    @Test
    void movieServiceUpdateMovieWithEmptyMovieTitleLengthEqualTo150TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        movieNo1.setTitle(newMovieTitle);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newMovieTitle, foundMovie.getTitle());
    }

    @Test
    void movieServiceUpdateMovieWithNegativeMovieBasePriceTestNegative() {
        double newMovieBasePrice = -1.00;
        movieNo1.setBasePrice(newMovieBasePrice);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithMovieBasePriceTooHighTestNegative() {
        double newMovieBasePrice = 101.00;
        movieNo1.setBasePrice(newMovieBasePrice);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithMovieBasePriceEqualTo0TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        double newMovieBasePrice = 0;
        movieNo1.setBasePrice(newMovieBasePrice);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getBasePrice());
    }

    @Test
    void movieServiceUpdateMovieWithMovieBasePriceEqualTo100TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        double newMovieBasePrice = 100;
        movieNo1.setBasePrice(newMovieBasePrice);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newMovieBasePrice, foundMovie.getBasePrice());
    }

    @Test
    void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        int newScrRoomNumber = 0;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {
        int newScrRoomNumber = 31;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws MovieServiceReadException {
        int newScrRoomNumber = 1;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newScrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    void movieServiceUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        int newScrRoomNumber = 30;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newScrRoomNumber, foundMovie.getScrRoomNumber());
    }

    @Test
    void movieServiceUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        int newNumberOfAvailableSeats = -1;
        movieNo1.setScrRoomNumber(newNumberOfAvailableSeats);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        int newNumberOfAvailableSeats = 121;
        movieNo1.setScrRoomNumber(newNumberOfAvailableSeats);
        assertThrows(MovieServiceUpdateException.class, () -> movieService.update(movieNo1));
    }

    @Test
    void movieServiceUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        int newNumberOfAvailableSeats = 0;
        movieNo1.setAvailableSeats(newNumberOfAvailableSeats);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newNumberOfAvailableSeats, foundMovie.getAvailableSeats());
    }

    @Test
    void movieServiceUpdateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws MovieServiceReadException, MovieServiceUpdateException {
        int newNumberOfAvailableSeats = 120;
        movieNo1.setAvailableSeats(newNumberOfAvailableSeats);
        movieService.update(movieNo1);
        Movie foundMovie = movieService.findByUUID(movieNo1.getId());
        assertNotNull(foundMovie);
        assertEquals(newNumberOfAvailableSeats, foundMovie.getAvailableSeats());
    }

    // Delete tests

    @Test
    void movieServiceDeleteMovieTestPositive() throws MovieServiceReadException, MovieServiceDeleteException {
        UUID removedMovieUUID = movieNo1.getId();
        Movie foundMovie = movieService.findByUUID(removedMovieUUID);
        assertNotNull(foundMovie);
        assertEquals(movieNo1, foundMovie);
        movieService.delete(removedMovieUUID);
        assertThrows(MovieServiceReadException.class, () -> movieService.findByUUID(removedMovieUUID));
    }

    @Test
    void movieServiceDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        Movie movie = new Movie(UUID.randomUUID(), "SomeExampleTitleNo1", 25.75, 4, 40);
        assertNotNull(movie);
        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(movie.getId()));
    }
}