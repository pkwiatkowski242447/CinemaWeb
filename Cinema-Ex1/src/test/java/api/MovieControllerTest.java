package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.dto.MovieDTO;
import pl.pas.gr3.cinema.dto.MovieInputDTO;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerReadException;
import pl.pas.gr3.cinema.managers.implementations.MovieManager;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MovieControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MovieControllerTest.class);
    private static String moviesBaseURL;

    private static final String databaseName = "default";
    private static MovieRepository movieRepository;
    private static MovieManager movieManager;

    private Movie movieNo1;
    private Movie movieNo2;

    @BeforeAll
    public static void init() {
        movieRepository = new MovieRepository(databaseName);
        movieManager = new MovieManager(movieRepository);

        moviesBaseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/movies";
        RestAssured.baseURI = moviesBaseURL;
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearCollection();
        try {
            movieNo1 = movieManager.create("ExampleMovieTitleNo1", 38.45, 5, 40);
            movieNo2 = movieManager.create("ExampleMovieTitleNo1", 32.60, 2, 75);
        } catch (MovieManagerCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearCollection();
    }

    private void clearCollection() {
        try {
            List<Movie> listOfMovies = movieManager.findAll();
            for (Movie movie : listOfMovies) {
                movieManager.delete(movie.getMovieID());
            }
        } catch (MovieManagerReadException | MovieManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        movieManager.close();
    }

    // Create tests

    @Test
    public void movieControllerCreateMovieTestPositive() throws Exception {
        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithNullMovieTitleTestNegative() throws Exception {
        String movieTitle = null;
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithEmptyMovieTitleTestNegative() throws Exception {
        String movieTitle = "";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieTitleTooShortTestNegative() throws Exception {
        String movieTitle = "";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieTitleTooLongTestNegative() throws Exception {
        String movieTitle = "";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieTitleLengthEqualTo1TestPositive() throws Exception {
        String movieTitle = "d";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieTitleLengthEqualTo150TestPositive() throws Exception {
        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithNegativeMovieBasePriceTestNegative() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = -1;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieBasePriceTooHighTestNegative() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 101.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieBasePriceEqualTo0TestPositive() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 0;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithMovieBasePriceEqualTo100TestPositive() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithScreeningRoomNumberEqualTo0TestNegative() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 45.00;
        int scrRoomNumber = 0;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithScreeningRoomNumberTooHighTestNegative() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 45.00;
        int scrRoomNumber = 31;
        int numberOfAvailableSeats = 37;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 1;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 100;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 30;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 45.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = -1;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 45.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 121;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 45.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 0;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws Exception {
        String movieTitle = "SomeExampleMovieTitleNo1";
        double movieBasePrice = 45.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 120;
        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(movieInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(moviesBaseURL);
            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);
            assertNotNull(movieDTO);
            assertEquals(movieTitle, movieDTO.getMovieTitle());
            assertEquals(movieBasePrice, movieDTO.getMovieBasePrice());
            assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
            assertEquals(numberOfAvailableSeats, movieDTO.getNumberOfAvailableSeats());
        }
    }

    // Read tests

    @Test
    public void movieControllerFindMovieByIDTestPositive() throws Exception {
        UUID searchedMovieID = movieNo1.getMovieID();
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String path = moviesBaseURL + "/" + searchedMovieID;

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);

            Response response = requestSpecification.get(path);

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(200);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            MovieDTO movieDTO = jsonb.fromJson(response.asString(), MovieDTO.class);

            assertEquals(movieNo1.getMovieID(), movieDTO.getMovieID());
            assertEquals(movieNo1.getMovieTitle(), movieDTO.getMovieTitle());
            assertEquals(movieNo1.getScrRoomNumber(), movieDTO.getScrRoomNumber());
            assertEquals(movieNo1.getNumberOfAvailableSeats(), movieDTO.getNumberOfAvailableSeats());
        }
    }

    @Test
    public void movieControllerFindMovieByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedMovieID = UUID.randomUUID();
        String path = moviesBaseURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());
    }

    @Test
    public void movieControllerFindMovieByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedMovieTitle = "NonExistentMovieTitle";
        String path = moviesBaseURL + "/login/" + searchedMovieTitle;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());
    }

    @Test
    public void movieControllerFindAllMoviesTestPositive() {
        String path = moviesBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());
    }

    // Update tests

    @Test
    public void movieControllerUpdateMovieTestPositive() throws Exception {
        String movieTitleBefore = movieNo1.getMovieTitle();
        double movieBasePriceBefore = movieNo1.getMovieBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getNumberOfAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieNo1.setMovieTitle(newMovieTitle);
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        String movieTitleAfter = foundMovie.getMovieTitle();
        double movieBasePriceAfter = foundMovie.getMovieBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getNumberOfAvailableSeats();

        assertEquals(newMovieTitle, movieTitleAfter);
        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);

        assertNotEquals(movieTitleBefore, movieTitleAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithNullMovieTitleTestNegative() throws Exception {
        String newMovieTitle = null;
        movieNo1.setMovieTitle(newMovieTitle);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithEmptyMovieTitleTestNegative() throws Exception {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithEmptyMovieTitleTooShortTestNegative() throws Exception {
        String newMovieTitle = "";
        movieNo1.setMovieTitle(newMovieTitle);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithEmptyMovieTitleTooLongTestNegative() throws Exception {
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        movieNo1.setMovieTitle(newMovieTitle);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithEmptyMovieTitleLengthEqualTo1TestPositive() throws Exception {
        String movieTitleBefore = movieNo1.getMovieTitle();
        String newMovieTitle = "d";
        movieNo1.setMovieTitle(newMovieTitle);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        String movieTitleAfter = foundMovie.getMovieTitle();

        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithEmptyMovieTitleLengthEqualTo150TestPositive() throws Exception {
        String movieTitleBefore = movieNo1.getMovieTitle();
        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        movieNo1.setMovieTitle(newMovieTitle);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        String movieTitleAfter = foundMovie.getMovieTitle();

        assertEquals(newMovieTitle, movieTitleAfter);
        assertNotEquals(movieTitleBefore, movieTitleAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithNegativeMovieBasePriceTestNegative() throws Exception {
        double newMovieBasePrice = -1.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithMovieBasePriceTooHighTestNegative() throws Exception {
        double newMovieBasePrice = 101.00;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithMovieBasePriceEqualTo0TestPositive() throws Exception {
        double movieBasePriceBefore = movieNo1.getMovieBasePrice();
        double newMovieBasePrice = 0;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        double movieBasePriceAfter = foundMovie.getMovieBasePrice();

        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithMovieBasePriceEqualTo100TestPositive() throws Exception {
        double movieBasePriceBefore = movieNo1.getMovieBasePrice();
        double newMovieBasePrice = 100;
        movieNo1.setMovieBasePrice(newMovieBasePrice);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        double movieBasePriceAfter = foundMovie.getMovieBasePrice();

        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() throws Exception {
        int newScrRoomNumber = -1;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithScreeningRoomNumberTooHighTestNegative() throws Exception {
        int newScrRoomNumber = 31;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() throws Exception {
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int newScrRoomNumber = 1;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();

        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() throws Exception {
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int newScrRoomNumber = 30;
        movieNo1.setScrRoomNumber(newScrRoomNumber);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();

        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() throws Exception {
        int newNumberOfAvailableSeats = -1;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() throws Exception {
        int newNumberOfAvailableSeats = 121;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void movieControllerUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() throws Exception {
        int numberOfAvailableSeatsBefore = movieNo1.getNumberOfAvailableSeats();
        int newNumberOfAvailableSeats = 0;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        int numberOfAvailableSeatsAfter = foundMovie.getNumberOfAvailableSeats();

        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }

    @Test
    public void movieControllerUpdateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() throws Exception {
        int numberOfAvailableSeatsBefore = movieNo1.getNumberOfAvailableSeats();
        int newNumberOfAvailableSeats = 120;
        movieNo1.setNumberOfAvailableSeats(newNumberOfAvailableSeats);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            MovieDTO movieDTO = new MovieDTO(movieNo1.getMovieID(), movieNo1.getMovieTitle(), movieNo1.getMovieBasePrice(), movieNo1.getScrRoomNumber(), movieNo1.getNumberOfAvailableSeats());
            MovieDTO[] arr = new MovieDTO[1];
            arr[0] = movieDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(moviesBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Movie foundMovie = movieManager.findByUUID(movieNo1.getMovieID());

        int numberOfAvailableSeatsAfter = foundMovie.getNumberOfAvailableSeats();

        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }

    // Delete tests

    @Test
    public void movieControllerDeleteMovieTestPositive() throws Exception {
        UUID removedMovieID = movieNo1.getMovieID();
        String path = moviesBaseURL + "/" + removedMovieID;

        Movie foundMovie = movieManager.findByUUID(removedMovieID);

        assertNotNull(foundMovie);
        assertEquals(movieNo1.getMovieTitle(), foundMovie.getMovieTitle());
        assertEquals(movieNo1.getMovieBasePrice(), foundMovie.getMovieBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), foundMovie.getScrRoomNumber());
        assertEquals(movieNo1.getNumberOfAvailableSeats(), foundMovie.getNumberOfAvailableSeats());

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        assertThrows(MovieManagerReadException.class, () -> movieManager.findByUUID(removedMovieID));
    }

    @Test
    public void movieControllerDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        UUID removedMovieID = UUID.randomUUID();
        String path = moviesBaseURL + "/" + removedMovieID;

        assertThrows(MovieManagerReadException.class, () -> movieManager.findByUUID(removedMovieID));

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);

    }
}
