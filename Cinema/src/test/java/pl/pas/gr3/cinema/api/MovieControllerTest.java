package pl.pas.gr3.cinema.api;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pas.gr3.cinema.TestConstants;
import pl.pas.gr3.cinema.exception.not_found.MovieNotFoundException;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.dto.auth.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.output.MovieDTO;
import pl.pas.gr3.cinema.dto.input.MovieInputDTO;
import pl.pas.gr3.cinema.service.impl.MovieServiceImpl;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.repository.impl.MovieRepositoryImpl;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MovieControllerTest {

    private static MovieRepositoryImpl movieRepository;
    private static AccountRepositoryImpl accountRepository;
    private static MovieServiceImpl movieService;
    private static PasswordEncoder passwordEncoder;

    private Movie movieNo1;
    private Movie movieNo2;
    private Client clientUser;
    private Admin adminUser;
    private Staff staffUser;
    private static String passwordNotHashed;

    @BeforeAll
    static void init() {
        movieRepository = new MovieRepositoryImpl(TestConstants.databaseName);
        accountRepository = new AccountRepositoryImpl(TestConstants.databaseName);
        movieService = new MovieServiceImpl(movieRepository);

        passwordEncoder = new BCryptPasswordEncoder();

        ClassLoader classLoader = AuthenticationControllerTest.class.getClassLoader();
        URL resourceURL = classLoader.getResource("pas-truststore.jks");

        RestAssured.config = RestAssuredConfig.newConfig().sslConfig(
            new SSLConfig().trustStore(resourceURL.getPath(), "password")
                .and()
                .port(8000)
                .and()
                .allowAllHostnames()
        );

        passwordNotHashed = "password";
    }

    @BeforeEach
    void initializeSampleData() {
        clearCollection();

        try {
            clientUser = accountRepository.createClient("ClientLoginX", passwordEncoder.encode(passwordNotHashed));
            staffUser = accountRepository.createStaff("StaffLoginX", passwordEncoder.encode(passwordNotHashed));
            adminUser = accountRepository.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
        } catch (Exception exception) {
            throw new RuntimeException("Could not create sample users with userRepository object.", exception);
        }

        try {
            movieNo1 = movieService.create("ExampleMovieTitleNo1", 38.45, 5, 40);
            movieNo2 = movieService.create("ExampleMovieTitleNo1", 32.60, 2, 75);
        } catch (Exception exception) {
            throw new RuntimeException("Could not create sample movies with movieRepository object.", exception);
        }
    }

    @AfterEach
    void destroySampleData() {
        clearCollection();
    }

    private void clearCollection() {
        try {
            List<Client> clients = accountRepository.findAllClients();
            clients.forEach(client -> accountRepository.delete(client.getId(), UserConstants.CLIENT_DISCRIMINATOR));

            List<Admin> admins = accountRepository.findAllAdmins();
            admins.forEach(admin -> accountRepository.delete(admin.getId(), UserConstants.ADMIN_DISCRIMINATOR));

            List<Staff> staffs = accountRepository.findAllStaffs();
            staffs.forEach(staff -> accountRepository.delete(staff.getId(), UserConstants.STAFF_DISCRIMINATOR));
        } catch (Exception exception) {
            throw new RuntimeException("Could not delete sample users with userRepository object.", exception);
        }

        try {
            List<Movie> movies = movieService.findAll();
            movies.forEach(movie -> movieService.delete(movie.getId()));
        } catch (Exception exception) {
            throw new RuntimeException("Could not delete sample movies with movieRepository object.", exception);
        }
    }

    @AfterAll
    static void destroy() {
        movieRepository.close();
    }

    // Create tests

    @Test
    void movieControllerCreateMovieAsAnUnauthenticatedUserTestNegative() {
        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerCreateMovieAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerCreateMovieAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerCreateMovieWithNullMovieTitleAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = null;
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithEmptyMovieTitleAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithMovieTitleTooShortAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithMovieTitleTooLongAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithMovieTitleLengthEqualTo1AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "d";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithMovieTitleLengthEqualTo150AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithNegativeMovieBasePriceAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = -1;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithMovieBasePriceTooHighAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 101.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithMovieBasePriceEqualTo0AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 0;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithMovieBasePriceEqualTo100AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 100.00;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithScreeningRoomNumberEqualTo0AsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 0;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithScreeningRoomNumberTooHighAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 31;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithScreeningRoomNumberEqualTo1AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 1;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithScreeningRoomNumberEqualTo30TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 30;
        int numberOfAvailableSeats = 37;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = -1;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 121;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerCreateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 0;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerCreateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String movieTitle = "OtherExampleMovieTitleNo1";
        double movieBasePrice = 50.25;
        int scrRoomNumber = 11;
        int numberOfAvailableSeats = 120;

        MovieInputDTO movieInputDTO = new MovieInputDTO(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(movieInputDTO);

        Response response = requestSpecification.post(TestConstants.moviesURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertNotNull(movieDTO);
        assertEquals(movieTitle, movieDTO.getTitle());
        assertEquals(movieBasePrice, movieDTO.getBasePrice());
        assertEquals(scrRoomNumber, movieDTO.getScrRoomNumber());
        assertEquals(numberOfAvailableSeats, movieDTO.getAvailableSeats());
    }

    // Read tests

    @Test
    void movieControllerFindMovieByIDAsAnUnauthenticatedUserTestNegative() {
        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerFindMovieByIDAsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertEquals(movieNo1.getId(), movieDTO.getId());
        assertEquals(movieNo1.getTitle(), movieDTO.getTitle());
        assertEquals(movieNo1.getScrRoomNumber(), movieDTO.getScrRoomNumber());
        assertEquals(movieNo1.getAvailableSeats(), movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerFindMovieByIDAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        MovieDTO movieDTO = response.getBody().as(MovieDTO.class);

        assertEquals(movieNo1.getId(), movieDTO.getId());
        assertEquals(movieNo1.getTitle(), movieDTO.getTitle());
        assertEquals(movieNo1.getScrRoomNumber(), movieDTO.getScrRoomNumber());
        assertEquals(movieNo1.getAvailableSeats(), movieDTO.getAvailableSeats());
    }

    @Test
    void movieControllerFindMovieByIDAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerFindMovieByIDThatIsNotInTheDatabaseAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedMovieID = UUID.randomUUID();
        String path = TestConstants.moviesURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void movieControllerFindMovieByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedMovieID = UUID.randomUUID();
        String path = TestConstants.moviesURL + "/" + searchedMovieID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void movieControllerFindAllMoviesAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.moviesURL + "/all";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerFindAllMoviesAsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.moviesURL + "/all";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
    }

    @Test
    void movieControllerFindAllMoviesAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.moviesURL + "/all";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
    }

    @Test
    void movieControllerFindAllMoviesAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.moviesURL + "/all";
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Update tests

    @Test
    void movieControllerUpdateMovieAsAnUnauthenticatedUserTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerUpdateMovieAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerUpdateMovieAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerUpdateMovieWithoutIfMatchHeaderAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    void movieControllerUpdateMovieWithChangedIDAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        movieOutputDTO.setId(UUID.randomUUID());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithNullMovieTitleAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = null;
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithEmptyMovieTitleTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithMovieTitleTooShortTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithMovieTitleTooLongTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfd";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithMovieTitleLengthEqualTo1TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "d";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithMovieTitleLengthEqualTo150TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "ddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddfddddf";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithNegativeMovieBasePriceTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "OtherExampleMovieTitleNo1";
        double newMovieBasePrice = -1;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithMovieBasePriceTooHighTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "OtherExampleMovieTitleNo1";
        double newMovieBasePrice = 101.00;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithMovieBasePriceEqualTo0TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 0;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithMovieBasePriceEqualTo100TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 100.00;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithScreeningRoomNumberEqualTo0TestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "OtherExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 0;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithScreeningRoomNumberTooHighTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "OtherExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 31;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithScreeningRoomNumberEqualTo1TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 1;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithScreeningRoomNumberEqualTo30TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 30;
        int newNumberOfAvailableSeats = 11;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithNegativeNumberOfAvailableSeatsTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "OtherExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = -1;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithNumberOfAvailableSeatsTooHighTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newMovieTitle = "OtherExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 121;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerUpdateMovieWithNumberOfAvailableSeatsEqualTo0TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 0;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

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
    void movieControllerUpdateMovieWithNumberOfAvailableSeatsEqualTo120TestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.moviesURL + "/" + movieNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        MovieDTO movieOutputDTO = response.getBody().as(MovieDTO.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String movieTitleBefore = movieNo1.getTitle();
        double movieBasePriceBefore = movieNo1.getBasePrice();
        int scrRoomNumberBefore = movieNo1.getScrRoomNumber();
        int numberOfAvailableSeatsBefore = movieNo1.getAvailableSeats();

        String newMovieTitle = "SomeExampleMovieTitleNo1";
        double newMovieBasePrice = 45.27;
        int newScrRoomNumber = 7;
        int newNumberOfAvailableSeats = 120;

        movieOutputDTO.setTitle(newMovieTitle);
        movieOutputDTO.setBasePrice(newMovieBasePrice);
        movieOutputDTO.setScrRoomNumber(newScrRoomNumber);
        movieOutputDTO.setAvailableSeats(newNumberOfAvailableSeats);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", etagContent);
        requestSpecification.body(movieOutputDTO);

        response = requestSpecification.put(TestConstants.moviesURL + "/update");

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Movie foundMovie = movieService.findByUUID(movieNo1.getId());

        String movieTitleAfter = foundMovie.getTitle();
        double movieBasePriceAfter = foundMovie.getBasePrice();
        int scrRoomNumberAfter = foundMovie.getScrRoomNumber();
        int numberOfAvailableSeatsAfter = foundMovie.getAvailableSeats();

        assertEquals(newMovieTitle, movieTitleAfter);
        assertEquals(newMovieBasePrice, movieBasePriceAfter);
        assertEquals(newScrRoomNumber, scrRoomNumberAfter);
        assertEquals(newNumberOfAvailableSeats, numberOfAvailableSeatsAfter);

        assertNotEquals(movieTitleBefore, movieTitleAfter);
        assertNotEquals(movieBasePriceBefore, movieBasePriceAfter);
        assertNotEquals(scrRoomNumberBefore, scrRoomNumberAfter);
        assertNotEquals(numberOfAvailableSeatsBefore, numberOfAvailableSeatsAfter);
    }

    // Delete tests

    @Test
    void movieControllerDeleteMovieAsAnUnauthenticatedClientTestNegative() {
        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID;

        Movie foundMovie = movieService.findByUUID(removedMovieID);

        assertNotNull(foundMovie);
        assertEquals(movieNo1.getTitle(), foundMovie.getTitle());
        assertEquals(movieNo1.getBasePrice(), foundMovie.getBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), foundMovie.getScrRoomNumber());
        assertEquals(movieNo1.getAvailableSeats(), foundMovie.getAvailableSeats());

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerDeleteMovieAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID;

        Movie foundMovie = movieService.findByUUID(removedMovieID);

        assertNotNull(foundMovie);
        assertEquals(movieNo1.getTitle(), foundMovie.getTitle());
        assertEquals(movieNo1.getBasePrice(), foundMovie.getBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), foundMovie.getScrRoomNumber());
        assertEquals(movieNo1.getAvailableSeats(), foundMovie.getAvailableSeats());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerDeleteMovieAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        Movie foundMovie = movieService.findByUUID(removedMovieID);

        assertNotNull(foundMovie);
        assertEquals(movieNo1.getTitle(), foundMovie.getTitle());
        assertEquals(movieNo1.getBasePrice(), foundMovie.getBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), foundMovie.getScrRoomNumber());
        assertEquals(movieNo1.getAvailableSeats(), foundMovie.getAvailableSeats());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        assertThrows(MovieNotFoundException.class, () -> movieService.findByUUID(removedMovieID));
    }

    @Test
    void movieControllerDeleteMovieAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        Movie foundMovie = movieService.findByUUID(removedMovieID);

        assertNotNull(foundMovie);
        assertEquals(movieNo1.getTitle(), foundMovie.getTitle());
        assertEquals(movieNo1.getBasePrice(), foundMovie.getBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), foundMovie.getScrRoomNumber());
        assertEquals(movieNo1.getAvailableSeats(), foundMovie.getAvailableSeats());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerDeleteMovieThatIsNotInTheDatabaseTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID removedMovieID = UUID.randomUUID();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        assertThrows(MovieNotFoundException.class, () -> movieService.findByUUID(removedMovieID));

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    private String loginToAccount(LoginAccountRequest loginDto, String loginURL) {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(loginDto.login(), loginDto.password()));

        Response response = requestSpecification.post(loginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        return response.getBody().asString();
    }
}
