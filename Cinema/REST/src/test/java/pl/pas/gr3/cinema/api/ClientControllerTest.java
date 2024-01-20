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
import pl.pas.gr3.cinema.exceptions.services.crud.client.ClientServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.client.ClientServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.client.ClientServiceReadException;
import pl.pas.gr3.cinema.repositories.implementations.UserRepository;
import pl.pas.gr3.cinema.services.implementations.ClientService;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.dto.output.ClientDTO;
import pl.pas.gr3.dto.input.ClientInputDTO;
import pl.pas.gr3.dto.update.ClientPasswordDTO;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ClientControllerTest.class);
    private static String clientsBaseURL;

    private static final String databaseName = "default";
    private static UserRepository userRepository;
    private static ClientService clientService;

    private Client clientNo1;
    private Client clientNo2;

    @BeforeAll
    public static void init() {
        userRepository = new UserRepository(databaseName);
        clientService = new ClientService(userRepository);

        clientsBaseURL = "http://localhost:8000/api/v1/clients";
        RestAssured.baseURI = clientsBaseURL;
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearCollection();
        try {
            clientNo1 = clientService.create("UniqueClientLogNo1", "UniqueClientPasswordNo1");
            clientNo1 = clientService.create("UniqueClientLogNo2", "UniqueClientPasswordNo2");
        } catch (ClientServiceCreateException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearCollection();
    }

    private void clearCollection() {
        try {
            List<Client> listOfClients = clientService.findAll();
            for (Client client : listOfClients) {
                clientService.delete(client.getUserID());
            }
        } catch (ClientServiceReadException | ClientServiceDeleteException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
    }

    // Create tests

    @Test
    public void clientControllerCreateClientTestPositive() throws Exception {
        String clientLogin = "SecretClientLoginNo1";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            ClientDTO createdObject = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getClientID());
            assertEquals(createdObject.getClientLogin(), clientInputDTO.getClientLogin());
            assertTrue(createdObject.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerCreateClientWithNullLoginThatTestNegative() throws Exception {
        String clientLogin = null;
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithEmptyLoginThatTestNegative() throws Exception {
        String clientLogin = "";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithLoginTooShortThatTestNegative() throws Exception {
        String clientLogin = "ddddfdd";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithLoginTooLongThatTestNegative() throws Exception {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithLoginLengthEqualTo8ThatTestPositive() throws Exception {
        String clientLogin = "ddddfddd";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ClientDTO createdObject = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getClientID());
            assertEquals(createdObject.getClientLogin(), clientInputDTO.getClientLogin());
            assertTrue(createdObject.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerCreateClientWithLoginLengthEqualTo20ThatTestPositive() throws Exception {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ClientDTO createdObject = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getClientID());
            assertEquals(createdObject.getClientLogin(), clientInputDTO.getClientLogin());
            assertTrue(createdObject.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerCreateClientWithLoginThatDoesNotMeetRegExTestNegative() throws Exception {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithLoginThatIsAlreadyInTheDatabaseTestNegative() throws Exception {
        String clientLogin = clientNo1.getUserLogin();
        String clientPassword = "SecretClientPasswordNo1";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(409);
        }
    }

    @Test
    public void clientControllerCreateClientWithNullPasswordThatTestNegative() throws Exception {
        String clientLogin = "SecretClientLoginNo1";
        String clientPassword = null;
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithEmptyPasswordThatTestNegative() throws Exception {
        String clientLogin = "SecretClientLoginNo1";
        String clientPassword = "";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithPasswordTooShortThatTestNegative() throws Exception {
        String clientLogin = "SecretClientLogNo1";
        String clientPassword = "ddddfdd";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithPasswordTooLongThatTestNegative() throws Exception {
        String clientLogin = "SecretClientLogNo1";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerCreateClientWithPasswordLengthEqualTo8ThatTestPositive() throws Exception {
        String clientLogin = "SecretClientLogNo1";
        String clientPassword = "ddddfddd";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ClientDTO createdObject = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getClientID());
            assertEquals(createdObject.getClientLogin(), clientInputDTO.getClientLogin());
            assertTrue(createdObject.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerCreateClientWithPasswordLengthEqualTo40ThatTestPositive() throws Exception {
        String clientLogin = "SecretClientLogNo1";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ClientDTO createdObject = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getClientID());
            assertEquals(createdObject.getClientLogin(), clientInputDTO.getClientLogin());
            assertTrue(createdObject.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerCreateClientWithPasswordThatDoesNotMeetRegExTestNegative() throws Exception {
        String clientLogin = "SecretClientLogNo1";
        String clientPassword = "Some Invalid Password";
        ClientInputDTO clientInputDTO = new ClientInputDTO(clientLogin, clientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(clientInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    // Read tests

    @Test
    public void clientControllerFindClientByIDTestPositive() throws Exception {
        UUID searchedClientID = clientNo1.getUserID();
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String path = clientsBaseURL + "/" + searchedClientID;

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.urlEncodingEnabled(false);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.get(path);

            Response response = requestSpecification.get(path);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(200);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ClientDTO clientDTO = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertEquals(clientNo1.getUserID(), clientDTO.getClientID());
            assertEquals(clientNo1.getUserLogin(), clientDTO.getClientLogin());
            assertEquals(clientNo1.isUserStatusActive(), clientDTO.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedClientID = UUID.randomUUID();
        String path = clientsBaseURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    @Test
    public void clientControllerFindClientByLoginTestPositive() throws Exception {
        String searchedClientLogin = clientNo1.getUserLogin();
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String path = clientsBaseURL + "/login/" + searchedClientLogin;

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.accept(ContentType.JSON);

            Response response = requestSpecification.get(path);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(200);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ClientDTO clientDTO = jsonb.fromJson(response.asString(), ClientDTO.class);

            assertEquals(clientNo1.getUserID(), clientDTO.getClientID());
            assertEquals(clientNo1.getUserLogin(), clientDTO.getClientLogin());
            assertEquals(clientNo1.isUserStatusActive(), clientDTO.isClientStatusActive());
        }
    }

    @Test
    public void clientControllerFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedClientLogin = "SomeNonExistentLogin";
        String path = clientsBaseURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    @Test
    public void clientControllerFindAllClientsMatchingLoginTestPositive() throws Exception {
        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = clientsBaseURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    @Test
    public void clientControllerFindAllClientsTestPositive() {
        String path = clientsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    // Update tests

    @Test
    public void clientControllerUpdateClientTestPositive() throws Exception {
        String clientLoginBefore = clientNo1.getUserLogin();
        String clientPasswordBefore = clientNo1.getUserPassword();
        String newClientLogin = "SomeNewClientLogNo1";
        String newClientPassword = "SomeNewClientPasswordNo1";

        clientNo1.setUserLogin(newClientLogin);
        clientNo1.setUserPassword(newClientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());

        String clientLoginAfter = foundClient.getUserLogin();
        String clientPasswordAfter = foundClient.getUserPassword();

        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientControllerUpdateClientWithNullLoginTestNegative() throws Exception {
        String newClientLogin = null;
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithEmptyLoginTestNegative() throws Exception {
        String newClientLogin = "";
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithLoginTooShortTestNegative() throws Exception {
        String newClientLogin = "ddddfdd";
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithLoginTooLongTestNegative() throws Exception {
        String newClientLogin = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithLoginLengthEqualTo8TestPositive() throws Exception {
        String clientLoginBefore = clientNo1.getUserLogin();
        String newClientLogin = "ddddfddd";
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        String clientLoginAfter = foundClient.getUserLogin();

        assertEquals(newClientLogin, clientLoginAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
    }

    @Test
    public void clientControllerUpdateClientWithLoginLengthEqualTo20TestPositive() throws Exception {
        String clientLoginBefore = clientNo1.getUserLogin();
        String newClientLogin = "ddddfddddfddddfddddf";
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        String clientLoginAfter = foundClient.getUserLogin();

        assertEquals(newClientLogin, clientLoginAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
    }

    @Test
    public void clientControllerUpdateClientWithLoginThatViolatesRegExTestNegative() throws Exception {
        String newClientLogin = "Some Invalid Login";
        clientNo1.setUserLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithNullPasswordTestNegative() throws Exception {
        String newClientPassword = null;
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithEmptyPasswordTestNegative() throws Exception {
        String newClientPassword = "";
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithPasswordTooShortTestNegative() throws Exception {
        String newClientPassword = "ddddfdd";
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithPasswordTooLongTestNegative() throws Exception {
        String newClientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void clientControllerUpdateClientWithPasswordLengthEqualTo8TestPositive() throws Exception {
        String clientPasswordBefore = clientNo1.getUserPassword();
        String newClientPassword = "ddddfddd";
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        String clientPasswordAfter = foundClient.getUserPassword();

        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientControllerUpdateClientWithPasswordLengthEqualTo40TestPositive() throws Exception {
        String clientPasswordBefore = clientNo1.getUserPassword();
        String newClientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Client foundClient = clientService.findByUUID(clientNo1.getUserID());
        String clientPasswordAfter = foundClient.getUserPassword();

        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientControllerUpdateClientWithPasswordThatViolatesRegExTestNegative() throws Exception {
        String newClientPassword = "Some Invalid Password";
        clientNo1.setUserPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getUserID(), clientNo1.getUserLogin(), clientNo1.getUserPassword(), clientNo1.isUserStatusActive());
            ClientPasswordDTO[] arr = new ClientPasswordDTO[1];
            arr[0] = clientPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(clientsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    // Activate tests

    @Test
    public void clientControllerActivateClientTestPositive() throws Exception {
        UUID activatedClientID = clientNo1.getUserID();
        String path = clientsBaseURL + "/" + activatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(activatedClientID);
        boolean clientStatusActiveBefore = foundClient.isUserStatusActive();

        path = clientsBaseURL + "/" + activatedClientID + "/activate";

        requestSpecification = RestAssured.given();

        response = requestSpecification.post(path);

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        foundClient = clientService.findByUUID(activatedClientID);
        boolean clientStatusActiveAfter = foundClient.isUserStatusActive();

        assertTrue(clientStatusActiveAfter);
        assertFalse(clientStatusActiveBefore);
    }

    @Test
    public void clientControllerActivateClientThatIsNotInTheDatabaseTestNegative() {
        UUID activatedClientID = UUID.randomUUID();
        String path = clientsBaseURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    public void clientControllerDeactivateClientTestPositive() throws Exception {
        boolean clientStatusActiveBefore = clientNo1.isUserStatusActive();
        UUID deactivatedClientID = clientNo1.getUserID();
        String path = clientsBaseURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(deactivatedClientID);
        boolean clientStatusActiveAfter = foundClient.isUserStatusActive();

        assertFalse(clientStatusActiveAfter);
        assertTrue(clientStatusActiveBefore);
    }

    @Test
    public void clientControllerDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        UUID deactivatedClientID = UUID.randomUUID();
        String path = clientsBaseURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }
}
