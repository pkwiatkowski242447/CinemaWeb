package pl.pas.gr3.cinema.api;

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
import pl.pas.gr3.cinema.services.implementations.*;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.AdminServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.AdminServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.AdminServiceReadException;
import pl.pas.gr3.cinema.exceptions.services.crud.client.ClientServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.client.ClientServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.client.ClientServiceReadException;
import pl.pas.gr3.cinema.exceptions.services.crud.movie.MovieServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.movie.MovieServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.movie.MovieServiceReadException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceReadException;
import pl.pas.gr3.cinema.exceptions.services.crud.ticket.TicketServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.ticket.TicketServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.ticket.TicketServiceReadException;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.UserRepository;
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
    private static UserRepository userRepository;
    private static MovieRepository movieRepository;

    private static TicketService ticketService;
    private static ClientService clientService;
    private static AdminService adminService;
    private static StaffService staffService;
    private static MovieService movieService;

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
        baseURL = "http://localhost:8000/api/v1";
        ticketsBaseURL = baseURL + "/tickets";
        clientsBaseURL = baseURL + "/clients";
        adminsBaseURL = baseURL + "/admins";
        staffsBaseURL = baseURL + "/staffs";
        moviesBaseURL = baseURL + "/movies";

        ticketRepository = new TicketRepository(databaseName);
        userRepository = new UserRepository(databaseName);
        movieRepository = new MovieRepository(databaseName);

        ticketService = new TicketService(ticketRepository);
        clientService = new ClientService(userRepository);
        adminService = new AdminService(userRepository);
        staffService = new StaffService(userRepository);
        movieService = new MovieService(movieRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            clientNo1 = clientService.create("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = clientService.create("ClientLoginNo2", "ClientPasswordNo2");
        } catch (ClientServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            adminNo1 = adminService.create("AdminLoginNo1", "AdminPasswordNo1");
            adminNo2 = adminService.create("AdminLoginNo2", "AdminPasswordNo2");
        } catch (AdminServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            staffNo1 = staffService.create("StaffLoginNo1", "StaffPasswordNo1");
            staffNo2 = staffService.create("StaffLoginNo2", "StaffPasswordNo2");
        } catch (StaffServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            movieNo1 = movieService.create("ExampleMovieTitleNo1", 35.74, 4, 75);
            movieNo2 = movieService.create("ExampleMovieTitleNo1", 28.60, 5, 50);
        } catch (MovieServiceCreateException exception) {
            logger.error(exception.getMessage());
        }

        movieTimeNo1 = LocalDateTime.now().plusDays(2).plusHours(3).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusDays(3).plusHours(6).truncatedTo(ChronoUnit.SECONDS);

        try {
            ticketNo1 = ticketService.create(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
            ticketNo2 = ticketService.create(movieTimeNo2.toString(), clientNo1.getUserID(), movieNo2.getMovieID());

            ticketNo3 = ticketService.create(movieTimeNo1.toString(), adminNo1.getUserID(), movieNo1.getMovieID());
            ticketNo4 = ticketService.create(movieTimeNo2.toString(), adminNo1.getUserID(), movieNo2.getMovieID());

            ticketNo5 = ticketService.create(movieTimeNo1.toString(), staffNo1.getUserID(), movieNo1.getMovieID());
            ticketNo6 = ticketService.create(movieTimeNo2.toString(), staffNo1.getUserID(), movieNo2.getMovieID());
        } catch (TicketServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        try {
            List<Ticket> listOfTickets = ticketService.findAll();
            for (Ticket ticket : listOfTickets) {
                ticketService.delete(ticket.getTicketID());
            }
        } catch (TicketServiceReadException | TicketServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Movie> listOfMovies = movieService.findAll();
            for (Movie movie : listOfMovies) {
                movieService.delete(movie.getMovieID());
            }
        } catch (MovieServiceReadException | MovieServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Client> listOfClients = clientService.findAll();
            for (Client client : listOfClients) {
                clientService.delete(client.getUserID());
            }
        } catch (ClientServiceReadException | ClientServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Admin> listOfAdmins = adminService.findAll();
            for (Admin admin : listOfAdmins) {
                adminService.delete(admin.getUserID());
            }
        } catch (AdminServiceReadException | AdminServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Staff> listOfStaffs = staffService.findAll();
            for (Staff staff : listOfStaffs) {
                staffService.delete(staff.getUserID());
            }
        } catch (StaffServiceReadException | StaffServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        ticketRepository.close();
        userRepository.close();
        movieRepository.close();
    }

    // Create tests

    @Test
    public void ticketControllerCreateNormalTicketTestPositive() throws Exception {
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
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
            assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
            assertEquals(movieNo1.getMovieID(), ticketDTO.getMovieID());
            assertEquals(movieNo1.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    @Test
    public void ticketControllerCreateReducedTicketTestPositive() throws Exception {
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getUserID(), movieNo1.getMovieID());
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
            assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
            assertEquals(movieNo1.getMovieID(), ticketDTO.getMovieID());
            assertEquals(movieNo1.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    @Test
    public void ticketControllerCreateTicketWithNullClientTestNegative() throws Exception {
        UUID clientID = null;
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientID, movieNo1.getMovieID());
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
        TicketInputDTO ticketInputDTO = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getUserID(), movieID);
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
            assertEquals(ticketNo1.getUserID(), ticketDTO.getClientID());
            assertEquals(ticketNo1.getMovieID(), ticketDTO.getMovieID());
            assertEquals(ticketNo1.getTicketPrice(), ticketDTO.getTicketFinalPrice());
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
        requestSpecification.accept(ContentType.JSON);

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

        TicketDTO ticketDTO = new TicketDTO(ticketNo1.getTicketID(), ticketNo1.getMovieTime(), ticketNo1.getTicketPrice(), ticketNo1.getUserID(), ticketNo1.getMovieID());
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

        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getTicketID());

        LocalDateTime movieTimeAfter = foundTicket.getMovieTime();

        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketControllerUpdateTicketWithNullMovieTimeTestPositive() throws Exception {
        LocalDateTime newMovieTime = null;
        ticketNo1.setMovieTime(newMovieTime);
        TicketDTO ticketDTO = new TicketDTO(ticketNo1.getTicketID(), ticketNo1.getMovieTime(), ticketNo1.getTicketPrice(), ticketNo1.getUserID(), ticketNo1.getMovieID());
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
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getMovieBasePrice(), clientNo1.getUserID(), movieNo1.getMovieID());
        assertNotNull(ticket);
        TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID());
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

        Ticket foundTicket = ticketService.findByUUID(removedTicketID);

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(removedTicketID));
    }

    @Test
    public void ticketControllerDeleteTicketThatIsNotInTheDatabaseTestPositive() {
        UUID removedTicketID = UUID.randomUUID();
        String path = ticketsBaseURL + "/" + removedTicketID;

        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(removedTicketID));

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.delete(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);

    }

    @Test
    public void ticketControllerAllocateTwoTicketsOnePositiveAndOneNegativeTestPositive() throws Exception {
        Movie testMovie = movieService.create("SomeMovieTitleNo1", 31.20, 3, 1);
        TicketInputDTO ticketInputDTONo1 = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getUserID(), testMovie.getMovieID());
        TicketInputDTO ticketInputDTONo2 = new TicketInputDTO(movieTimeNo1.toString(), clientNo2.getUserID(), testMovie.getMovieID());
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
            assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
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
        Movie testMovie = movieService.create("SomeMovieTitleNo1", 31.20, 3, 1);
        TicketInputDTO ticketInputDTONo1 = new TicketInputDTO(movieTimeNo1.toString(), clientNo1.getUserID(), testMovie.getMovieID());
        TicketInputDTO ticketInputDTONo2 = new TicketInputDTO(movieTimeNo1.toString(), clientNo2.getUserID(), testMovie.getMovieID());
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
            assertEquals(clientNo1.getUserID(), ticketDTO.getClientID());
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
            assertEquals(clientNo2.getUserID(), ticketDTO.getClientID());
            assertEquals(testMovie.getMovieID(), ticketDTO.getMovieID());
            assertEquals(testMovie.getMovieBasePrice(), ticketDTO.getTicketFinalPrice());
        }
    }

    // Admin tests

    @Test
    public void adminControllerFindAllTicketsTestPositive() {
        UUID searchedAdminID = adminNo1.getUserID();
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
    public void clientControllerFindAllTicketsTestPositive() {
        UUID searchedClientID = clientNo1.getUserID();
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
    public void staffControllerFindAllTicketsTestPositive() {
        UUID searchedStaffID = staffNo1.getUserID();
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

        List<Ticket> listOfTicketForMovie = movieService.getListOfTicketsForCertainMovie(removedMovieID);
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