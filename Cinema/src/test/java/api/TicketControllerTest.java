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
import pl.pas.gr3.cinema.dto.TicketDTO;
import pl.pas.gr3.cinema.dto.TicketInputDTO;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.AdminManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.AdminManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.AdminManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.StaffManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.StaffManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.StaffManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerReadException;
import pl.pas.gr3.cinema.managers.implementations.*;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;
import pl.pas.gr3.cinema.repositories.implementations.TicketRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketControllerTest {

    private static String baseURL;
    private static String ticketsBaseURL;
    private static String adminsBaseURL;
    private static String clientsBaseURL;
    private static String staffsBaseURL;
    private static String moviesBaseURL;

    private static final Logger logger = LoggerFactory.getLogger(TicketControllerTest.class);
    private static final String databaseName = "default";

    private static TicketRepository ticketRepository;
    private static ClientRepository clientRepository;
    private static MovieRepository movieRepository;

    private static TicketManager ticketManager;
    private static ClientManager clientManager;
    private static AdminManager adminManager;
    private static StaffManager staffManager;
    private static MovieManager movieManager;

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

    @BeforeAll
    public static void init() {
        baseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api";
        ticketsBaseURL = baseURL + "/tickets";
        clientsBaseURL = baseURL + "/clients";
        adminsBaseURL = baseURL + "/admins";
        staffsBaseURL = baseURL + "/staffs";
        moviesBaseURL = baseURL + "/movies";

        ticketRepository = new TicketRepository(databaseName);
        clientRepository = new ClientRepository(databaseName);
        movieRepository = new MovieRepository(databaseName);

        ticketManager = new TicketManager(ticketRepository);
        clientManager = new ClientManager(clientRepository);
        adminManager = new AdminManager(clientRepository);
        staffManager = new StaffManager(clientRepository);
        movieManager = new MovieManager(movieRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            clientNo1 = clientManager.create("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = clientManager.create("ClientLoginNo2", "ClientPasswordNo2");
        } catch (ClientManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            adminNo1 = adminManager.create("AdminLoginNo1", "AdminPasswordNo1");
            adminNo2 = adminManager.create("AdminLoginNo2", "AdminPasswordNo2");
        } catch (AdminManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            staffNo1 = staffManager.create("StaffLoginNo1", "StaffPasswordNo1");
            staffNo2 = staffManager.create("StaffLoginNo2", "StaffPasswordNo2");
        } catch (StaffManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            movieNo1 = movieManager.create("ExampleMovieTitleNo1", 35.74, 4, 75);
            movieNo2 = movieManager.create("ExampleMovieTitleNo1", 28.60, 5, 50);
        } catch (MovieManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(3).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusDays(3).plusHours(6).truncatedTo(ChronoUnit.SECONDS);

        try {
            ticketNo1 = ticketManager.create(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), "normal");
            ticketNo2 = ticketManager.create(movieTimeNo2.toString(), clientNo1.getClientID(), movieNo2.getMovieID(), "normal");

            ticketNo3 = ticketManager.create(movieTimeNo1.toString(), adminNo1.getClientID(), movieNo1.getMovieID(), "normal");
            ticketNo4 = ticketManager.create(movieTimeNo2.toString(), adminNo1.getClientID(), movieNo2.getMovieID(), "normal");

            ticketNo5 = ticketManager.create(movieTimeNo1.toString(), staffNo1.getClientID(), movieNo1.getMovieID(), "normal");
            ticketNo6 = ticketManager.create(movieTimeNo2.toString(), staffNo1.getClientID(), movieNo2.getMovieID(), "normal");
        } catch (TicketManagerCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        try {
            List<Ticket> listOfTickets = ticketManager.findAll();
            for (Ticket ticket : listOfTickets) {
                ticketManager.delete(ticket.getTicketID());
            }
        } catch (TicketManagerReadException | TicketManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Movie> listOfMovies = movieManager.findAll();
            for (Movie movie : listOfMovies) {
                movieManager.delete(movie.getMovieID());
            }
        } catch (MovieManagerReadException | MovieManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Client> listOfClients = clientManager.findAll();
            for (Client client : listOfClients) {
                clientManager.delete(client.getClientID());
            }
        } catch (ClientManagerReadException | ClientManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Admin> listOfAdmins = adminManager.findAll();
            for (Admin admin : listOfAdmins) {
                adminManager.delete(admin.getClientID());
            }
        } catch (AdminManagerReadException | AdminManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Staff> listOfStaffs = staffManager.findAll();
            for (Staff staff : listOfStaffs) {
                staffManager.delete(staff.getClientID());
            }
        } catch (StaffManagerReadException | StaffManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        ticketManager.close();
        clientManager.close();
        adminManager.close();
        staffManager.close();
        movieManager.close();
    }

    // Create tests

    @Test
    public void ticketControllerCreateNormalTicketTestPositive() throws Exception {
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), "normal");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            TicketDTO ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertNotNull(ticketDTO.getTicketID());
            assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
            assertEquals(clientNo1.getClientID(), ticketDTO.getClientID());
            assertEquals(movieNo1.getMovieID(), ticketDTO.getMovieID());
            assertEquals(movieNo1.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    @Test
    public void ticketControllerCreateReducedTicketTestPositive() throws Exception {
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), "reduced");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            TicketDTO ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertNotNull(ticketDTO.getTicketID());
            assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
            assertEquals(clientNo1.getClientID(), ticketDTO.getClientID());
            assertEquals(movieNo1.getMovieID(), ticketDTO.getMovieID());
            assertEquals(movieNo1.getMovieBasePrice() * 0.75, ticketDTO.getTicketFinalPrice());
        }
    }

    @Test
    public void ticketControllerCreateTicketWithNullClientTestNegative() throws Exception {
        UUID clientID = null;
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientID, movieNo1.getMovieID(), "normal");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void ticketControllerCreateTicketWithNullMovieTestNegative() throws Exception {
        UUID movieID = null;
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getClientID(), movieID, "normal");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void ticketControllerCreateTicketWithEmptyTicketTypeTestNegative() throws Exception {
        String ticketType = "";
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), ticketType);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            TicketDTO ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertNotNull(ticketDTO.getTicketID());
            assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
            assertEquals(clientNo1.getClientID(), ticketDTO.getClientID());
            assertEquals(movieNo1.getMovieID(), ticketDTO.getMovieID());
            assertEquals(movieNo1.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    // Read tests

    @Test
    public void ticketControllerFindTicketByIDTestPositive() throws Exception {
        UUID searchedTicketID = ticketNo1.getTicketID();
        String path = ticketsBaseURL + "/" + searchedTicketID;
        try (Jsonb jsonb = JsonbBuilder.create()) {
            RequestSpecification requestSpecification = RestAssured.given();
            Response response = requestSpecification.get(path);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(200);

            TicketDTO ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertEquals(ticketNo1.getTicketID(), ticketDTO.getTicketID());
            assertEquals(ticketNo1.getMovieTime(), ticketDTO.getMovieTime());
            assertEquals(ticketNo1.getClient().getClientID(), ticketDTO.getClientID());
            assertEquals(ticketNo1.getMovie().getMovieID(), ticketDTO.getMovieID());
            assertEquals(ticketNo1.getTicketFinalPrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    @Test
    public void ticketControllerFindTicketByIDThatIsNotInTheDatabaseTestPositive() {
        UUID searchedTicketID = UUID.randomUUID();
        String path = ticketsBaseURL + "/" + searchedTicketID;

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    @Test
    public void ticketControllerFindAllTicketsTestPositive() {
        String path = ticketsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    // Update tests

    @Test
    public void ticketControllerUpdateTicketTestPositive() throws Exception {
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ticketNo1.setMovieTime(newMovieTime);

        TicketDTO ticketDTO = new TicketDTO(ticketNo1.getTicketID(), ticketNo1.getMovieTime(), ticketNo1.getTicketFinalPrice(), ticketNo1.getClient().getClientID(), ticketNo1.getMovie().getMovieID());
        try (Jsonb jsonb = JsonbBuilder.create()) {
            TicketDTO[] arr = new TicketDTO[1];
            arr[0] = ticketDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(ticketsBaseURL);

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Ticket foundTicket = ticketManager.findByUUID(ticketNo1.getTicketID());

        LocalDateTime movieTimeAfter = foundTicket.getMovieTime();

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketControllerUpdateTicketWithNullMovieTimeTestPositive() throws Exception {
        LocalDateTime newMovieTime = null;
        ticketNo1.setMovieTime(newMovieTime);
        TicketDTO ticketDTO = new TicketDTO(ticketNo1.getTicketID(), ticketNo1.getMovieTime(), ticketNo1.getTicketFinalPrice(), ticketNo1.getClient().getClientID(), ticketNo1.getMovie().getMovieID());
        try (Jsonb jsonb = JsonbBuilder.create()) {
            TicketDTO[] arr = new TicketDTO[1];
            arr[0] = ticketDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(ticketsBaseURL);

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void ticketControllerUpdateTicketThatIsNotInTheDatabaseTestNegative() throws Exception {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
        try (Jsonb jsonb = JsonbBuilder.create()) {
            TicketDTO[] arr = new TicketDTO[1];
            arr[0] = ticketDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(ticketsBaseURL);

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    // Delete tests

    @Test
    public void ticketControllerDeleteTicketTestPositive() throws Exception {
        UUID removedTicketID = ticketNo1.getTicketID();
        String path = ticketsBaseURL + "/" + removedTicketID;

        Ticket foundTicket = ticketManager.findByUUID(removedTicketID);

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        assertThrows(TicketManagerReadException.class, () -> ticketManager.findByUUID(removedTicketID));
    }

    @Test
    public void ticketControllerDeleteTicketThatIsNotInTheDatabaseTestPositive() {
        UUID removedTicketID = UUID.randomUUID();
        String path = ticketsBaseURL + "/" + removedTicketID;

        assertThrows(TicketManagerReadException.class, () -> ticketManager.findByUUID(removedTicketID));

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);

    }

    @Test
    public void ticketControllerAllocateTwoTicketsOnePositiveAndOneNegativeTestPositive() throws Exception {
        Movie testMovie = movieManager.create("SomeMovieTitleNo1", 31.20, 3, 1);
        TicketInputDTO ticketInputDTONo1 = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getClientID(), testMovie.getMovieID(), "normal");
        TicketInputDTO ticketInputDTONo2 = new TicketInputDTO(movieTimeNo1.toString(), clientNo2.getClientID(), testMovie.getMovieID(), "normal");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTONo1);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            TicketDTO ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertNotNull(ticketDTO.getTicketID());
            assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
            assertEquals(clientNo1.getClientID(), ticketDTO.getClientID());
            assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
            assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());

            jsonPayload = jsonb.toJson(ticketInputDTONo2);
            logger.info("Json: " + jsonPayload);

            requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void ticketControllerAllocateTwoTicketsTwoPositiveTestPositive() throws Exception {
        Movie testMovie = movieManager.create("SomeMovieTitleNo1", 31.20, 3, 1);
        TicketInputDTO ticketInputDTONo1 = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getClientID(), testMovie.getMovieID(), "normal");
        TicketInputDTO ticketInputDTONo2 = new TicketInputDTO(movieTimeNo1.toString(), clientNo2.getClientID(), testMovie.getMovieID(), "normal");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(ticketInputDTONo1);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            TicketDTO ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertNotNull(ticketDTO.getTicketID());
            assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
            assertEquals(clientNo1.getClientID(), ticketDTO.getClientID());
            assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
            assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());

            String path = ticketsBaseURL + "/" + ticketDTO.getTicketID();
            requestSpecification = RestAssured.given();

            response = requestSpecification.delete(path);

            jsonPayload = jsonb.toJson(ticketInputDTONo2);
            logger.info("Json: " + jsonPayload);

            requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            response = requestSpecification.post(ticketsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            validatableResponse = response.then();
            validatableResponse.statusCode(201);

            ticketDTO = jsonb.fromJson(response.asString(), TicketDTO.class);
            assertNotNull(ticketDTO);
            assertNotNull(ticketDTO.getTicketID());
            assertEquals(movieTimeNo1, ticketDTO.getMovieTime());
            assertEquals(clientNo2.getClientID(), ticketDTO.getClientID());
            assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
            assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    // Admin tests

    @Test
    public void adminControllerFindAllTicketsTicketTestPositive() {
        UUID searchedAdminID = adminNo1.getClientID();
        String path = adminsBaseURL + "/" + searchedAdminID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    // Client tests

    @Test
    public void clientControllerFindAllTicketsTicketTestPositive() {
        UUID searchedClientID = clientNo1.getClientID();
        String path = clientsBaseURL + "/" + searchedClientID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    // Staff tests

    @Test
    public void staffControllerFindAllTicketsTicketTestPositive() {
        UUID searchedStaffID = staffNo1.getClientID();
        String path = staffsBaseURL + "/" + searchedStaffID + "/ticket-list";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    // Movie tests

    @Test
    public void movieControllerDeleteMovieThatIsUsedInTicketTestNegative() {
        UUID removedMovieID = movieNo1.getMovieID();
        String path = moviesBaseURL + "/" + removedMovieID;

        List<Ticket> listOfTicketForMovie = movieManager.getListOfTicketsForCertainMovie(removedMovieID);
        assertNotNull(listOfTicketForMovie);
        assertFalse(listOfTicketForMovie.isEmpty());
        assertEquals(3, listOfTicketForMovie.size());

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.delete(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }
}
