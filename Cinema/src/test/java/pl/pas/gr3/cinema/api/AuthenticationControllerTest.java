package pl.pas.gr3.cinema.api;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pas.gr3.cinema.TestConstants;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.dto.account.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.account.AccountResponse;

import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AuthenticationControllerTest {

    private static AccountRepositoryImpl accountRepository;

    private static PasswordEncoder passwordEncoder;

    private static Client clientUser;
    private static Admin adminUser;
    private static Staff staffUser;
    private static String passwordNotHashed;

    @BeforeAll
    static void init() {
        accountRepository = new AccountRepositoryImpl();

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
            adminUser = accountRepository.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
            staffUser = accountRepository.createStaff("StaffLoginX", passwordEncoder.encode(passwordNotHashed));
        } catch (Exception exception) {
            throw new RuntimeException("Could not create sample users with userRepository object.", exception);
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
    }

    // Register methods
    // User registration as an unauthenticated user.

    // Authorization tests.

    @Test
    void authorizationControllerRegisterClientAsAnUnauthenticatedUserTestPositive() {
        String clientLogin = "x1ClientLogin";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterStaffAsAnUnauthenticatedUserTestNegative() {
        String staffLogin = "x1StaffLogin";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void authorizationControllerRegisterAdminAsAnUnauthenticatedUserTestNegative() {
        String adminLogin = "x1AdminLogin";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Data incorrect tests.

    // Login errors.

    @Test
    void authorizationControllerRegisterClientWithNullLoginAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = null;
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithEmptyLoginAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooShortAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooLongAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnUnauthenticatedClientTestPositive() {
        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnUnauthenticatedClientTestPositive() {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = "Some Login";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnUnauthenticatedUserTestNegative() {
        String clientLogin = clientUser.getLogin();
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // User registration as an authenticated client.

    // Authorization tests.

    @Test
    void authorizationControllerRegisterClientAsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "x2ClientLogin";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterStaffAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String staffLogin = "x2StaffLogin";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void authorizationControllerRegisterAdminAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String adminLogin = "x2AdminLogin";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Data incorrect tests.

    // Login errors.

    @Test
    void authorizationControllerRegisterClientWithNullLoginAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = null;
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithEmptyLoginAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooShortAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooLongAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnAuthenticatedClientTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = "Test Login";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientLogin = clientUser.getLogin();
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // User registration as an authenticated staff.

    // Authorization tests.

    @Test
    void authorizationControllerRegisterClientAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "x3ClientLogin";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterStaffAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String staffLogin = "x3StaffLogin";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void authorizationControllerRegisterAdminAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String adminLogin = "x3AdminLogin";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Data incorrect tests.

    // Login errors.

    @Test
    void authorizationControllerRegisterClientWithNullLoginAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = null;
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithEmptyLoginAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooShortAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooLongAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse userOutputDTO = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, userOutputDTO.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = "Test Login";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String clientLogin = clientUser.getLogin();
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // User registration as an authenticated admin.

    // Authorization tests.

    @Test
    void authorizationControllerRegisterClientAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "x4ClientLogin";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterStaffAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "x4StaffLogin";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterAdminAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "x4AdminLogin";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(adminLogin, accountResponse.getLogin());
    }

    // Data incorrect tests.

    // Login errors.

    // Client

    @Test
    void authorizationControllerRegisterClientWithNullLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = null;
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithEmptyLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooShortAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfdd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginTooLongAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo8AsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfddd";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginOfLengthEqualTo20AsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatDoesNotMatchRegexAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = "Test Login";
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterClientWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String clientLogin = clientUser.getLogin();
        String clientPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(clientLogin, clientPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.clientRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // Staff

    @Test
    void authorizationControllerRegisterStaffWithNullLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = null;
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterStaffWithEmptyLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterStaffWithLoginTooShortAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfdd";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterStaffWithLoginTooLongAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterStaffWithLoginOfLengthEqualTo8AsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfddd";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterStaffWithLoginOfLengthEqualTo20AsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterStaffWithLoginThatDoesNotMatchRegexAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = "Test Login";
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterStaffWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String staffLogin = staffUser.getLogin();
        String staffPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(staffLogin, staffPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.staffRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // Admin

    @Test
    void authorizationControllerRegisterAdminWithNullLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = null;
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterAdminWithEmptyLoginAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterAdminWithLoginTooShortAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfdd";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterAdminWithLoginTooLongAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterAdminWithLoginOfLengthEqualTo8AsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfddd";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(adminLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterAdminWithLoginOfLengthEqualTo20AsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(201);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(adminLogin, accountResponse.getLogin());
    }

    @Test
    void authorizationControllerRegisterAdminWithLoginThatDoesNotMatchRegexAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = "Test Login";
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    @Test
    void authorizationControllerRegisterAdminWithLoginThatIsAlreadyInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String adminLogin = adminUser.getLogin();
        String adminPassword = "password";

        LoginAccountRequest userInputDTO = new LoginAccountRequest(adminLogin, adminPassword);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(userInputDTO);

        Response response = requestSpecification.post(TestConstants.adminRegisterURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(409);
    }

    // Login methods

    // Client

    @Test
    void authorizationControllerLoginToClientAccountTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.clientLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        assertFalse(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToClientAccountWithWrongLoginTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(clientUser.getLogin() + "1", passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.clientLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToClientAccountWithWrongPasswordTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed + "1"));

        Response response = requestSpecification.post(TestConstants.clientLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToClientAccountThatIsDisabledTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.post(TestConstants.clientsURL + "/" + clientUser.getId() + "/deactivate");
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed));

        response = requestSpecification.post(TestConstants.clientLoginURL);
        validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Staff

    @Test
    void authorizationControllerLoginToStaffAccountTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.staffLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        assertFalse(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToStaffAccountWithWrongLoginTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(staffUser.getLogin() + "1", passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.staffLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToStaffAccountWithWrongPasswordTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed + "1"));

        Response response = requestSpecification.post(TestConstants.staffLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToStaffAccountThatIsDisabledTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.post(TestConstants.staffsURL + "/" + staffUser.getId() + "/deactivate");
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed));

        response = requestSpecification.post(TestConstants.staffLoginURL);
        validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    // Admin

    @Test
    void authorizationControllerLoginToAdminAccountTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        assertFalse(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToAdminAccountWithWrongPasswordTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUser.getLogin() + "1", passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToAdminAccountWithWrongLoginTestPositive() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed + "1"));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
        assertTrue(response.getBody().asString().isEmpty());
    }

    @Test
    void authorizationControllerLoginToAdminAccountThatIsDisabledTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.post(TestConstants.staffsURL + "/" + adminUser.getId() + "/deactivate");
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed));

        response = requestSpecification.post(TestConstants.adminLoginURL);
        validatableResponse = response.then();

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
