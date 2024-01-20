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
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceCreateException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceDeleteException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceReadException;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.UserRepository;
import pl.pas.gr3.cinema.services.implementations.StaffService;
import pl.pas.gr3.dto.output.StaffDTO;
import pl.pas.gr3.dto.input.StaffInputDTO;
import pl.pas.gr3.dto.update.StaffPasswordDTO;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StaffControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(StaffControllerTest.class);
    private static String staffsBaseURL;

    private static final String databaseName = "default";
    private static UserRepository userRepository;
    private static StaffService staffManager;

    private Staff staffNo1;
    private Staff staffNo2;

    @BeforeAll
    public static void init() {
        userRepository = new UserRepository(databaseName);
        staffManager = new StaffService(userRepository);

        staffsBaseURL = "http://localhost:8000/api/v1/staffs";
        RestAssured.baseURI = staffsBaseURL;
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearCollection();
        try {
            staffNo1 = staffManager.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffManager.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        } catch (StaffServiceCreateException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearCollection();
    }

    private void clearCollection() {
        try {
            List<Staff> listOfStaff = staffManager.findAll();
            for (Staff staff : listOfStaff) {
                staffManager.delete(staff.getUserID());
            }
        } catch (StaffServiceReadException | StaffServiceDeleteException exception) {
            logger.debug(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        userRepository.close();
    }

    // Create tests

    @Test
    public void staffControllerCreateStaffTestPositive() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            StaffDTO createdObject = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getStaffID());
            assertEquals(createdObject.getStaffLogin(), staffInputDTO.getStaffLogin());
            assertTrue(createdObject.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerCreateStaffWithNullLoginThatTestNegative() throws Exception {
        String staffLogin = null;
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithEmptyLoginThatTestNegative() throws Exception {
        String staffLogin = "";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithLoginTooShortThatTestNegative() throws Exception {
        String staffLogin = "ddddfdd";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithLoginTooLongThatTestNegative() throws Exception {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithLoginLengthEqualTo8ThatTestPositive() throws Exception {
        String staffLogin = "ddddfddd";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            StaffDTO createdObject = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getStaffID());
            assertEquals(createdObject.getStaffLogin(), staffInputDTO.getStaffLogin());
            assertTrue(createdObject.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerCreateStaffWithLoginLengthEqualTo20ThatTestPositive() throws Exception {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            StaffDTO createdObject = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getStaffID());
            assertEquals(createdObject.getStaffLogin(), staffInputDTO.getStaffLogin());
            assertTrue(createdObject.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerCreateStaffWithLoginThatDoesNotMeetRegExTestNegative() throws Exception {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithLoginThatIsAlreadyInTheDatabaseTestNegative() throws Exception {
        String staffLogin = staffNo1.getUserLogin();
        String staffPassword = "SecretStaffPasswordNo1";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(409);
        }
    }

    @Test
    public void staffControllerCreateStaffWithNullPasswordThatTestNegative() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = null;
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithEmptyPasswordThatTestNegative() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithPasswordTooShortThatTestNegative() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "ddddfdd";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithPasswordTooLongThatTestNegative() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerCreateStaffWithPasswordLengthEqualTo8ThatTestPositive() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "ddddfddd";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            StaffDTO createdObject = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getStaffID());
            assertEquals(createdObject.getStaffLogin(), staffInputDTO.getStaffLogin());
            assertTrue(createdObject.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerCreateStaffWithPasswordLengthEqualTo40ThatTestPositive() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(201);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            StaffDTO createdObject = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertNotNull(createdObject);
            assertNotNull(createdObject.getStaffID());
            assertEquals(createdObject.getStaffLogin(), staffInputDTO.getStaffLogin());
            assertTrue(createdObject.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerCreateStaffWithPasswordThatDoesNotMeetRegExTestNegative() throws Exception {
        String staffLogin = "SecretStaffLoginNo1";
        String staffPassword = "Some Invalid Password";
        StaffInputDTO staffInputDTO = new StaffInputDTO(staffLogin, staffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            String jsonPayload = jsonb.toJson(staffInputDTO);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.accept(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.post(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    // Read tests

    @Test
    public void staffControllerFindStaffByIDTestPositive() throws Exception {
        UUID searchedStaffID = staffNo1.getUserID();
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String path = staffsBaseURL + "/" + searchedStaffID;

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

            StaffDTO staffDTO = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertEquals(staffNo1.getUserID(), staffDTO.getStaffID());
            assertEquals(staffNo1.getUserLogin(), staffDTO.getStaffLogin());
            assertEquals(staffNo1.isUserStatusActive(), staffDTO.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        UUID searchedStaffID = UUID.randomUUID();
        String path = staffsBaseURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);

        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    @Test
    public void staffControllerFindStaffByLoginTestPositive() throws Exception {
        String searchedStaffLogin = staffNo1.getUserLogin();
        try (Jsonb jsonb = JsonbBuilder.create()) {
            String path = staffsBaseURL + "/login/" + searchedStaffLogin;

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.accept(ContentType.JSON);

            Response response = requestSpecification.get(path);
            logger.info(response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(200);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            StaffDTO staffDTO = jsonb.fromJson(response.asString(), StaffDTO.class);

            assertEquals(staffNo1.getUserID(), staffDTO.getStaffID());
            assertEquals(staffNo1.getUserLogin(), staffDTO.getStaffLogin());
            assertEquals(staffNo1.isUserStatusActive(), staffDTO.isStaffStatusActive());
        }
    }

    @Test
    public void staffControllerFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        String searchedStaffLogin = "SomeNonExistentLogin";
        String path = staffsBaseURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        assertNotNull(response.asString());
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    @Test
    public void staffControllerFindFindStaffsMatchingLoginTestPositive() throws Exception {
        staffManager.create("ExtraStaffLogin", "ExtraStaffPassword");
        String matchedLogin = "Extra";
        String path = staffsBaseURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    @Test
    public void staffControllerFindFindStaffsTestPositive() {
        String path = staffsBaseURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        logger.info("Response: " + response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);
    }

    // Update tests

    @Test
    public void staffControllerUpdateStaffTestPositive() throws Exception {
        String staffLoginBefore = staffNo1.getUserLogin();
        String staffPasswordBefore = staffNo1.getUserPassword();
        String newStaffLogin = "SomeNewStaffLoginNo1";
        String newStaffPassword = "SomeNewStaffPasswordNo1";

        staffNo1.setUserLogin(newStaffLogin);
        staffNo1.setUserPassword(newStaffPassword);

        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Staff foundStaff = staffManager.findByUUID(staffNo1.getUserID());

        String staffLoginAfter = foundStaff.getUserLogin();
        String staffPasswordAfter = foundStaff.getUserPassword();

        assertEquals(newStaffLogin, staffLoginAfter);
        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    public void staffControllerUpdateStaffWithNullLoginTestNegative() throws Exception {
        String newStaffLogin = null;
        staffNo1.setUserLogin(newStaffLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithEmptyLoginTestNegative() throws Exception {
        String newStaffLogin = "";
        staffNo1.setUserLogin(newStaffLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithLoginTooShortTestNegative() throws Exception {
        String newStaffLogin = "ddddfdd";
        staffNo1.setUserLogin(newStaffLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithLoginTooLongTestNegative() throws Exception {
        String newStaffsLogin = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        staffNo1.setUserLogin(newStaffsLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithLoginLengthEqualTo8TestPositive() throws Exception {
        String staffLoginBefore = staffNo1.getUserLogin();
        String newStaffLogin = "ddddfddd";
        staffNo1.setUserLogin(newStaffLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Staff foundStaff = staffManager.findByUUID(staffNo1.getUserID());
        String staffLoginAfter = foundStaff.getUserLogin();

        assertEquals(newStaffLogin, staffLoginAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
    }

    @Test
    public void staffControllerUpdateStaffWithLoginLengthEqualTo20TestPositive() throws Exception {
        String staffLoginBefore = staffNo1.getUserLogin();
        String newStaffLogin = "ddddfddddfddddfddddf";
        staffNo1.setUserLogin(newStaffLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Staff foundStaff = staffManager.findByUUID(staffNo1.getUserID());
        String staffLoginAfter = foundStaff.getUserLogin();

        assertEquals(newStaffLogin, staffLoginAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
    }

    @Test
    public void staffControllerUpdateStaffWithLoginThatViolatesRegExTestNegative() throws Exception {
        String newStaffLogin = "Some Invalid Login";
        staffNo1.setUserLogin(newStaffLogin);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithNullPasswordTestNegative() throws Exception {
        String newStaffPassword = null;
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithEmptyPasswordTestNegative() throws Exception {
        String newStaffPassword = "";
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithPasswordTooShortTestNegative() throws Exception {
        String newStaffPassword = "ddddfdd";
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithPasswordTooLongTestNegative() throws Exception {
        String newStaffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    @Test
    public void staffControllerUpdateStaffWithPasswordLengthEqualTo8TestPositive() throws Exception {
        String staffPasswordBefore = staffNo1.getUserPassword();
        String newStaffPassword = "ddddfddd";
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Staff foundStaff = staffManager.findByUUID(staffNo1.getUserID());
        String staffPasswordAfter = foundStaff.getUserPassword();

        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    public void staffControllerUpdateStaffWithPasswordLengthEqualTo40TestPositive() throws Exception {
        String staffPasswordBefore = staffNo1.getUserPassword();
        String newStaffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertTrue(response.asString().isEmpty());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(204);
        }

        Staff foundStaff = staffManager.findByUUID(staffNo1.getUserID());
        String staffPasswordAfter = foundStaff.getUserPassword();

        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    public void staffControllerUpdateStaffWithPasswordThatViolatesRegExTestNegative() throws Exception {
        String newStaffPassword = "Some Invalid Password";
        staffNo1.setUserPassword(newStaffPassword);
        try (Jsonb jsonb = JsonbBuilder.create()) {
            StaffPasswordDTO staffPasswordDTO = new StaffPasswordDTO(staffNo1.getUserID(), staffNo1.getUserLogin(), staffNo1.getUserPassword(), staffNo1.isUserStatusActive());
            StaffPasswordDTO[] arr = new StaffPasswordDTO[1];
            arr[0] = staffPasswordDTO;
            String jsonPayload = jsonb.toJson(arr[0]);
            logger.info("Json: " + jsonPayload);

            RequestSpecification requestSpecification = RestAssured.given();
            requestSpecification.contentType(ContentType.JSON);
            requestSpecification.body(jsonPayload);

            Response response = requestSpecification.put(staffsBaseURL);

            assertNotNull(response.asString());
            logger.info("Response: " + response.asString());

            ValidatableResponse validatableResponse = response.then();
            validatableResponse.statusCode(400);
        }
    }

    // Activate tests

    @Test
    public void staffControllerActivateStaffTestPositive() throws Exception {
        UUID activatedStaffID = staffNo1.getUserID();
        String path = staffsBaseURL + "/" + activatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Staff foundStaff = staffManager.findByUUID(activatedStaffID);
        boolean staffStatusActiveBefore = foundStaff.isUserStatusActive();

        path = staffsBaseURL + "/" + activatedStaffID + "/activate";

        requestSpecification = RestAssured.given();

        response = requestSpecification.post(path);

        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        foundStaff = staffManager.findByUUID(activatedStaffID);
        boolean staffStatusActiveAfter = foundStaff.isUserStatusActive();

        assertTrue(staffStatusActiveAfter);
        assertFalse(staffStatusActiveBefore);
    }

    @Test
    public void staffControllerActivateStaffThatIsNotInTheDatabaseTestNegative() {
        UUID activatedStaffID = UUID.randomUUID();
        String path = staffsBaseURL + "/" + activatedStaffID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    public void staffControllerDeactivateStaffTestPositive() throws Exception {
        boolean staffStatusActiveBefore = staffNo1.isUserStatusActive();
        UUID deactivatedStaffID = staffNo1.getUserID();
        String path = staffsBaseURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Staff foundStaff = staffManager.findByUUID(deactivatedStaffID);
        boolean staffStatusActiveAfter = foundStaff.isUserStatusActive();

        assertFalse(staffStatusActiveAfter);
        assertTrue(staffStatusActiveBefore);
    }

    @Test
    public void staffControllerDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        UUID deactivatedStaffID = UUID.randomUUID();
        String path = staffsBaseURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();

        Response response = requestSpecification.post(path);

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }
}
