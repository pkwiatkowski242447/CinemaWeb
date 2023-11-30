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
import pl.pas.gr3.cinema.dto.users.*;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerReadException;
import pl.pas.gr3.cinema.managers.implementations.ClientManager;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceTest.class);
    private static String clientsBaseURL;

    private static final String databaseName = "default";
    private static ClientRepository clientRepository;
    private static ClientManager clientManager;

    private Client clientNo1;
    private Client clientNo2;

    @BeforeAll
    public static void init() {
        clientRepository = new ClientRepository(databaseName);
        clientManager = new ClientManager(clientRepository);

        clientsBaseURL = "http://localhost:8080/Cinema-1.0-SNAPSHOT/api/clients";
        RestAssured.baseURI = clientsBaseURL;
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearCollection();
        try {
            clientNo1 = clientManager.create("UniqueClientLogNo1", "UniqueClientPasswordNo1");
            clientNo1 = clientManager.create("UniqueClientLogNo2", "UniqueClientPasswordNo2");
        } catch (ClientManagerCreateException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearCollection();
    }

    private void clearCollection() {
        try {
            List<Client> listOfClients = clientManager.findAll();
            for (Client client : listOfClients) {
                clientManager.delete(client.getClientID());
            }
        } catch (ClientManagerReadException | ClientManagerDeleteException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        clientManager.close();
    }

    // Create tests

    @Test
    public void clientServiceCreateClientTestPositive() throws Exception {
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
    public void clientServiceCreateClientWithNullLoginThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithEmptyLoginThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithLoginTooShortThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithLoginTooLongThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithLoginLengthEqualTo8ThatTestPositive() throws Exception {
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
    public void clientServiceCreateClientWithLoginLengthEqualTo20ThatTestPositive() throws Exception {
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
    public void clientServiceCreateClientWithLoginThatDoesNotMeetRegExTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithLoginThatIsAlreadyInTheDatabaseTestNegative() throws Exception {
        String clientLogin = clientNo1.getClientLogin();
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
    public void clientServiceCreateClientWithNullPasswordThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithEmptyPasswordThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithPasswordTooShortThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithPasswordTooLongThatTestNegative() throws Exception {
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
    public void clientServiceCreateClientWithPasswordLengthEqualTo8ThatTestPositive() throws Exception {
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
    public void clientServiceCreateClientWithPasswordLengthEqualTo40ThatTestPositive() throws Exception {
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
    public void clientServiceCreateClientWithPasswordThatDoesNotMeetRegExTestNegative() throws Exception {
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
    public void clientServiceFindClientByIDTestPositive() throws Exception {
        UUID searchedClientID = clientNo1.getClientID();
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

            assertEquals(clientNo1.getClientID(), clientDTO.getClientID());
            assertEquals(clientNo1.getClientLogin(), clientDTO.getClientLogin());
            assertEquals(clientNo1.isClientStatusActive(), clientDTO.isClientStatusActive());
        }
    }

    @Test
    public void clientServiceFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedClientID = UUID.randomUUID();
        String path = clientsBaseURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void clientServiceFindClientByLoginTestPositive() throws Exception {
        String searchedClientLogin = clientNo1.getClientLogin();
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

            assertEquals(clientNo1.getClientID(), clientDTO.getClientID());
            assertEquals(clientNo1.getClientLogin(), clientDTO.getClientLogin());
            assertEquals(clientNo1.isClientStatusActive(), clientDTO.isClientStatusActive());
        }
    }

    @Test
    public void clientServiceFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedClientLogin = "SomeNonExistentLogin";
        String path = clientsBaseURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    public void clientServiceFindAllClientsMatchingLoginTestPositive() throws Exception {
        clientManager.create("ExtraClientLogin", "ExtraClientPassword");
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
    public void clientServiceFindAllClientsTestPositive() {
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
    public void clientServiceUpdateClientTestPositive() throws Exception {
        String clientLoginBefore = clientNo1.getClientLogin();
        String clientPasswordBefore = clientNo1.getClientPassword();
        String newClientLogin = "SomeNewClientLogNo1";
        String newClientPassword = "SomeNewClientPasswordNo1";

        clientNo1.setClientLogin(newClientLogin);
        clientNo1.setClientPassword(newClientPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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

        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());

        String clientLoginAfter = foundClient.getClientLogin();
        String clientPasswordAfter = foundClient.getClientPassword();

        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientServiceUpdateClientWithNullLoginTestNegative() throws Exception {
        String newClientLogin = null;
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithEmptyLoginTestNegative() throws Exception {
        String newClientLogin = "";
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithLoginTooShortTestNegative() throws Exception {
        String newClientLogin = "ddddfdd";
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithLoginTooLongTestNegative() throws Exception {
        String newClientLogin = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithLoginLengthEqualTo8TestPositive() throws Exception {
        String clientLoginBefore = clientNo1.getClientLogin();
        String newClientLogin = "ddddfddd";
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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

        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        String clientLoginAfter = foundClient.getClientLogin();

        assertEquals(newClientLogin, clientLoginAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
    }

    @Test
    public void clientServiceUpdateClientWithLoginLengthEqualTo20TestPositive() throws Exception {
        String clientLoginBefore = clientNo1.getClientLogin();
        String newClientLogin = "ddddfddddfddddfddddf";
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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

        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        String clientLoginAfter = foundClient.getClientLogin();

        assertEquals(newClientLogin, clientLoginAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
    }

    @Test
    public void clientServiceUpdateClientWithLoginThatViolatesRegExTestNegative() throws Exception {
        String newClientLogin = "Some Invalid Login";
        clientNo1.setClientLogin(newClientLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithNullPasswordTestNegative() throws Exception {
        String newClientPassword = null;
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithEmptyPasswordTestNegative() throws Exception {
        String newClientPassword = "";
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithPasswordTooShortTestNegative() throws Exception {
        String newClientPassword = "ddddfdd";
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithPasswordTooLongTestNegative() throws Exception {
        String newClientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceUpdateClientWithPasswordLengthEqualTo8TestPositive() throws Exception {
        String clientPasswordBefore = clientNo1.getClientPassword();
        String newClientPassword = "ddddfddd";
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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

        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        String clientPasswordAfter = foundClient.getClientPassword();

        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientServiceUpdateClientWithPasswordLengthEqualTo40TestPositive() throws Exception {
        String clientPasswordBefore = clientNo1.getClientPassword();
        String newClientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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

        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        String clientPasswordAfter = foundClient.getClientPassword();

        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientServiceUpdateClientWithPasswordThatViolatesRegExTestNegative() throws Exception {
        String newClientPassword = "Some Invalid Password";
        clientNo1.setClientPassword(newClientPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            ClientPasswordDTO clientPasswordDTO = new ClientPasswordDTO(clientNo1.getClientID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
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
    public void clientServiceActivateClientTestPositive() throws Exception {
        UUID activatedClientID = clientNo1.getClientID();
        String path = clientsBaseURL + "/" + activatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Client foundClient = clientManager.findByUUID(activatedClientID);
        boolean clientStatusActiveBefore = foundClient.isClientStatusActive();

        path = clientsBaseURL + "/" + activatedClientID + "/activate";

        requestSpecification = RestAssured.given();

        response = requestSpecification.post(path);

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        foundClient = clientManager.findByUUID(activatedClientID);
        boolean clientStatusActiveAfter = foundClient.isClientStatusActive();

        assertTrue(clientStatusActiveAfter);
        assertFalse(clientStatusActiveBefore);
    }

    @Test
    public void clientServiceActivateClientThatIsNotInTheDatabaseTestNegative() {
        UUID activatedClientID = UUID.randomUUID();
        String path = clientsBaseURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    public void clientServiceDeactivateClientTestPositive() throws Exception {
        boolean clientStatusActiveBefore = clientNo1.isClientStatusActive();
        UUID deactivatedClientID = clientNo1.getClientID();
        String path = clientsBaseURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Client foundClient = clientManager.findByUUID(deactivatedClientID);
        boolean clientStatusActiveAfter = foundClient.isClientStatusActive();

        assertFalse(clientStatusActiveAfter);
        assertTrue(clientStatusActiveBefore);
    }

    @Test
    public void clientServiceDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        UUID deactivatedClientID = UUID.randomUUID();
        String path = clientsBaseURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }
}
