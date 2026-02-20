package pl.pas.gr3.cinema.api;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pas.gr3.cinema.TestConstants;
import pl.pas.gr3.cinema.exception.not_found.TicketNotFoundException;
import pl.pas.gr3.cinema.service.impl.AdminServiceImpl;
import pl.pas.gr3.cinema.service.impl.ClientServiceImpl;
import pl.pas.gr3.cinema.service.impl.MovieServiceImpl;
import pl.pas.gr3.cinema.service.impl.StaffServiceImpl;
import pl.pas.gr3.cinema.service.impl.TicketServiceImpl;
import pl.pas.gr3.cinema.dto.auth.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.input.CreateOwnTicketRequest;
import pl.pas.gr3.cinema.dto.output.TicketResponse;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.repository.impl.MovieRepositoryImpl;
import pl.pas.gr3.cinema.repository.impl.TicketRepositoryImpl;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TicketControllerTest {

    private static TicketRepositoryImpl ticketRepository;
    private static AccountRepositoryImpl accountRepository;
    private static MovieRepositoryImpl movieRepository;

    private static TicketServiceImpl ticketService;
    private static ClientServiceImpl clientService;
    private static AdminServiceImpl adminService;
    private static StaffServiceImpl staffService;
    private static MovieServiceImpl movieService;
    private static PasswordEncoder passwordEncoder;

    private Client clientNo1;
    private Client clientNo2;

    private Admin adminNo1;
    private Admin adminNo2;

    private Staff staffNo1;
    private Staff staffNo2;

    private Movie movieNo1;
    private Movie movieNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;
    private Ticket ticketNo4;
    private Ticket ticketNo5;
    private Ticket ticketNo6;

    private LocalDateTime movieTimeNo1;
    private LocalDateTime movieTimeNo2;
    private static String passwordNotHashed;

    @BeforeAll
    static void init() {
        ticketRepository = new TicketRepositoryImpl(TestConstants.databaseName);
        accountRepository = new AccountRepositoryImpl(TestConstants.databaseName);
        movieRepository = new MovieRepositoryImpl(TestConstants.databaseName);

        ticketService = new TicketServiceImpl(accountRepository, ticketRepository);
        clientService = new ClientServiceImpl(accountRepository);
        adminService = new AdminServiceImpl(accountRepository);
        staffService = new StaffServiceImpl(accountRepository);
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
        try {
            clientNo1 = clientService.create("ClientLoginNo1", passwordEncoder.encode(passwordNotHashed));
            clientNo2 = clientService.create("ClientLoginNo2", passwordEncoder.encode(passwordNotHashed));
            
            adminNo1 = adminService.create("AdminLoginNo1", passwordEncoder.encode(passwordNotHashed));
            adminNo2 = adminService.create("AdminLoginNo2", passwordEncoder.encode(passwordNotHashed));

            staffNo1 = staffService.create("StaffLoginNo1", passwordEncoder.encode(passwordNotHashed));
            staffNo2 = staffService.create("StaffLoginNo2", passwordEncoder.encode(passwordNotHashed));

            movieNo1 = movieService.create("ExampleMovieTitleNo1", 35.74, 4, 75);
            movieNo2 = movieService.create("ExampleMovieTitleNo1", 28.60, 5, 50);

            ticketNo1 = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
            ticketNo2 = ticketService.create(movieTimeNo2.toString(), clientNo1.getId(), movieNo2.getId());

            ticketNo3 = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
            ticketNo4 = ticketService.create(movieTimeNo2.toString(), clientNo2.getId(), movieNo2.getId());

            ticketNo5 = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
            ticketNo6 = ticketService.create(movieTimeNo2.toString(), clientNo2.getId(), movieNo2.getId());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterEach
    void destroySampleData() {
        try {
            List<Ticket> tickets = ticketService.findAll();
            tickets.forEach(ticket -> ticketService.delete(ticket.getId()));

            List<Movie> movies = movieService.findAll();
            movies.forEach(movie -> movieService.delete(movie.getId()));

            List<Client> clients = clientService.findAll();
            clients.forEach(client -> clientService.delete(client.getId()));

            List<Admin> admins = adminService.findAll();
            admins.forEach(admin -> adminService.delete(admin.getId()));

            List<Staff> staffs = staffService.findAll();
            staffs.forEach(staff -> staffService.delete(staff.getId()));
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterAll
    static void destroy() {
        ticketRepository.close();
        accountRepository.close();
        movieRepository.close();
    }

    // Create tests

    @Test
    void ticketControllerCreateTicketAsAnUnauthenticatedUserTestNegative() {
        CreateOwnTicketRequest createOwnTicketRequest = new CreateOwnTicketRequest(movieTimeNo1.toString(), movieNo1.getId());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequest);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerCreateTicketAsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        CreateOwnTicketRequest createOwnTicketRequest = new CreateOwnTicketRequest(movieTimeNo1.toString(), movieNo1.getId());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequest);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);

        assertNotNull(ticketResponse);
        assertNotNull(ticketResponse.getId());
        assertEquals(movieTimeNo1, ticketResponse.getMovieTime());
        assertEquals(clientNo1.getId(), ticketResponse.getClientId());
        assertEquals(movieNo1.getId(), ticketResponse.getMovieId());
        assertEquals(movieNo1.getBasePrice(), ticketResponse.getFinalPrice());
    }

    @Test
    void ticketControllerCreateTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        CreateOwnTicketRequest createOwnTicketRequest = new CreateOwnTicketRequest(movieTimeNo1.toString(), movieNo1.getId());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequest);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerCreateTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        CreateOwnTicketRequest createOwnTicketRequest = new CreateOwnTicketRequest(movieTimeNo1.toString(), movieNo1.getId());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequest);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerCreateTicketWithNullMovieAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID movieID = null;

        CreateOwnTicketRequest createOwnTicketRequest = new CreateOwnTicketRequest(movieTimeNo1.toString(), movieID);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequest);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    // Read tests

    @Test
    void ticketControllerFindTicketByIDAsAnUnauthenticatedUserTestNegative() {
        UUID searchedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerFindTicketByIDAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);

        assertNotNull(ticketResponse);
        assertEquals(ticketNo1.getId(), ticketResponse.getId());
        assertEquals(ticketNo1.getMovieTime(), ticketResponse.getMovieTime());
        assertEquals(ticketNo1.getUserId(), ticketResponse.getClientId());
        assertEquals(ticketNo1.getMovieId(), ticketResponse.getMovieId());
        assertEquals(ticketNo1.getPrice(), ticketResponse.getFinalPrice());
    }

    @Test
    void ticketControllerFindTicketByIDAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);

        assertNotNull(ticketResponse);
        assertEquals(ticketNo1.getId(), ticketResponse.getId());
        assertEquals(ticketNo1.getMovieTime(), ticketResponse.getMovieTime());
        assertEquals(ticketNo1.getUserId(), ticketResponse.getClientId());
        assertEquals(ticketNo1.getMovieId(), ticketResponse.getMovieId());
        assertEquals(ticketNo1.getPrice(), ticketResponse.getFinalPrice());
    }

    @Test
    void ticketControllerFindTicketByIDAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerFindTicketByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedTicketID = UUID.randomUUID();
        String path = TestConstants.ticketsURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void ticketControllerFindAllTicketsAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerFindAllTicketsAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerFindAllTicketsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketResponse> listOfTickets = response.getBody().as(new TypeRef<>() {});
        assertEquals(6, listOfTickets.size());
    }

    @Test
    void ticketControllerFindAllTicketsAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.ticketsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Update tests

    @Test
    void ticketControllerUpdateTicketAsAnUnauthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerUpdateTicketAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getId());

        LocalDateTime movieTimeAfter = foundTicket.getMovieTime();

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    void ticketControllerUpdateTicketWithoutIfMatchHeaderAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);

        accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);
        
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    void ticketControllerUpdateTicketWithChangedTicketIDAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.getHeader(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        ticketResponse.setId(UUID.randomUUID());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void ticketControllerUpdateTicketAsAnAuthenticatedClientThatIsNotTheOwnerOfTheTicketTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(clientNo2.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);
        
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerUpdateTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerUpdateTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerUpdateTicketWithNullMovieTimeAsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.ticketsURL + "/" + ticketNo1.getId());
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        LocalDateTime newMovieTime = null;
        ticketResponse.setMovieTime(newMovieTime);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(ticketResponse);

        response = requestSpecification.put(TestConstants.ticketsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Delete tests

    @Test
    void ticketControllerDeleteTicketAsAnUnauthenticatedUserTestNegative() {
        UUID removedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerDeleteTicketAsAnAuthenticatedClientThatIsOwnerOfTheTicketTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        assertThrows(TicketNotFoundException.class, () -> ticketService.findByUUID(removedTicketID));
    }

    @Test
    void ticketControllerDeleteTicketAsAnAuthenticatedClientThatIsNotTheOwnerOfTheTicketTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo2.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerDeleteTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID removedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerDeleteTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID removedTicketID = ticketNo1.getId();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);
        assertNotNull(foundTicket);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void ticketControllerDeleteTicketThatIsNotInTheDatabaseAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedTicketID = UUID.randomUUID();
        String path = TestConstants.ticketsURL + "/" + removedTicketID + "/delete";

        assertThrows(TicketNotFoundException.class, () -> ticketService.findByUUID(removedTicketID));

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void ticketControllerAllocateTwoTicketsOnePositiveAndOneNegativeTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        Movie testMovie = movieService.create("SomeMovieTitleNo1", 31.20, 3, 1);
        CreateOwnTicketRequest createOwnTicketRequestNo1 = new CreateOwnTicketRequest(movieTimeNo1.toString(), testMovie.getId());
        CreateOwnTicketRequest createOwnTicketRequestNo2 = new CreateOwnTicketRequest(movieTimeNo1.toString(), testMovie.getId());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequestNo1);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);

        assertNotNull(ticketResponse);
        assertNotNull(ticketResponse.getId());
        assertEquals(movieTimeNo1, ticketResponse.getMovieTime());
        assertEquals(clientNo1.getId(), ticketResponse.getClientId());
        assertEquals(testMovie.getId(), ticketResponse.getMovieId());
        assertEquals(testMovie.getBasePrice(), ticketResponse.getFinalPrice());

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequestNo2);

        response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void ticketControllerAllocateTwoTicketsTwoPositiveTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        Movie testMovie = movieService.create("SomeMovieTitleNo1", 31.20, 3, 1);
        CreateOwnTicketRequest createOwnTicketRequestNo1 = new CreateOwnTicketRequest(movieTimeNo1.toString(), testMovie.getId());
        CreateOwnTicketRequest createOwnTicketRequestNo2 = new CreateOwnTicketRequest(movieTimeNo1.toString(), testMovie.getId());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequestNo1);

        Response response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        TicketResponse ticketResponse = response.getBody().as(TicketResponse.class);

        assertNotNull(ticketResponse);
        assertNotNull(ticketResponse.getId());
        assertEquals(movieTimeNo1, ticketResponse.getMovieTime());
        assertEquals(clientNo1.getId(), ticketResponse.getClientId());
        assertEquals(testMovie.getId(), ticketResponse.getMovieId());
        assertEquals(testMovie.getBasePrice(), ticketResponse.getFinalPrice());

        String path = TestConstants.ticketsURL + "/" + ticketResponse.getId() + "/delete";
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        response = requestSpecification.delete(path);
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(createOwnTicketRequestNo2);

        response = requestSpecification.post(TestConstants.ticketsURL + "/self");
        validatableResponse = response.then();

        validatableResponse.statusCode(201);

        ticketResponse = response.getBody().as(TicketResponse.class);

        assertNotNull(ticketResponse);
        assertNotNull(ticketResponse.getId());
        assertEquals(movieTimeNo1, ticketResponse.getMovieTime());
        assertEquals(clientNo1.getId(), ticketResponse.getClientId());
        assertEquals(testMovie.getId(), ticketResponse.getMovieId());
        assertEquals(testMovie.getBasePrice(), ticketResponse.getFinalPrice());
    }

    // Client tests

    @Test
    void clientControllerFindAllTicketsAsAnUnauthenticatedUserTestNegative() {
        UUID searchedClientID = clientNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindAllTicketsAsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.clientsURL + "/self/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketResponse> listOfTickets = response.getBody().as(new TypeRef<>() {});
        assertEquals(4, listOfTickets.size());
    }

    @Test
    void clientControllerFindAllTicketsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedClientID = clientNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketResponse> listOfTickets = response.getBody().as(new TypeRef<>() {});
        assertEquals(4, listOfTickets.size());
    }

    @Test
    void clientControllerFindAllTicketsAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = clientNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Movie tests

    @Test
    void movieControllerFindAllTicketsAsAnUnauthenticatedUserTestNegative() {
        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerFindAllTicketsAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerFindAllTicketsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<TicketResponse> listOfTickets = response.getBody().as(new TypeRef<>() {});
        assertEquals(3, listOfTickets.size());
    }

    @Test
    void movieControllerFindAllTicketsAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + searchedMovieID + "/tickets";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerDeleteMovieThatIsUsedInTicketAsAnUnauthenticatedUserTestNegative() {
        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTicketsForCertainMovie(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerDeleteMovieThatIsUsedInTicketAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTicketsForCertainMovie(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void movieControllerDeleteMovieThatIsUsedInTicketAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTicketsForCertainMovie(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void movieControllerDeleteMovieThatIsUsedInTicketAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID removedMovieID = movieNo1.getId();
        String path = TestConstants.moviesURL + "/" + removedMovieID + "/delete";

        List<Ticket> listOfTicketForMovie = movieService.getListOfTicketsForCertainMovie(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.delete(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
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