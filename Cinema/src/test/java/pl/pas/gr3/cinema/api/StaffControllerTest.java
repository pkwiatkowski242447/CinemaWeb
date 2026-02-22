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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pas.gr3.cinema.TestConstants;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.service.impl.StaffServiceImpl;
import pl.pas.gr3.cinema.dto.account.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.account.AccountResponse;
import pl.pas.gr3.cinema.dto.account.UpdateAccountRequest;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class StaffControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(StaffControllerTest.class);

    private static AccountRepositoryImpl accountRepository;
    private static StaffServiceImpl staffService;
    private static PasswordEncoder passwordEncoder;

    private Client clientUser;
    private Staff staffUserNo1;
    private Staff staffUserNo2;
    private Admin adminUser;
    private static String passwordNotHashed;

    @BeforeAll
    static void init() {
        accountRepository = new AccountRepositoryImpl(TestConstants.databaseName);
        staffService = new StaffServiceImpl(accountRepository);

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
            staffUserNo1 = accountRepository.createStaff("StaffLoginX1", passwordEncoder.encode(passwordNotHashed));
            staffUserNo2 = accountRepository.createStaff("StaffLoginX2", passwordEncoder.encode(passwordNotHashed));
            adminUser = accountRepository.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
        } catch (Exception exception) {
            logger.debug(exception.getMessage());
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

    @AfterAll
    static void destroy() {
        accountRepository.close();
    }

    // Read tests

    @Test
    void staffControllerFindStaffByIDAsAnUnauthenticatedUserTestNegative() {
        UUID searchedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindStaffByIDAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindStaffByIDAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffUserNo1.getId(), accountResponse.getId());
        assertEquals(staffUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(staffUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void staffControllerFindStaffByIDAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffUserNo1.getId(), accountResponse.getId());
        assertEquals(staffUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(staffUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void staffControllerFindStaffByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedStaffID = UUID.randomUUID();

        String path = TestConstants.staffsURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void staffControllerFindStaffByIDThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedStaffID = UUID.randomUUID();

        String path = TestConstants.staffsURL + "/" + searchedStaffID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void staffControllerFindStaffByLoginAsAnUnauthenticatedUserTestNegative() {
        String searchedStaffLogin = staffUserNo1.getLogin();
        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindStaffByLoginAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String searchedStaffLogin = staffUserNo1.getLogin();
        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindStaffByLoginAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedStaffLogin = staffUserNo1.getLogin();
        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffUserNo1.getId(), accountResponse.getId());
        assertEquals(staffUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(staffUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void staffControllerFindStaffByLoginAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedStaffLogin = staffUserNo1.getLogin();
        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(staffUserNo1.getId(), accountResponse.getId());
        assertEquals(staffUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(staffUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void staffControllerFindStaffByLoginThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedStaffLogin = "SomeNonExistentLogin";
        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void staffControllerFindStaffByLoginThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedStaffLogin = "SomeNonExistentLogin";
        String path = TestConstants.staffsURL + "/login/" + searchedStaffLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void staffControllerFindFindStaffsMatchingLoginAsAnUnauthenticatedUserTestNegative() {
        staffService.create("ExtraStaffLogin", "ExtraStaffPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.staffsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindFindStaffsMatchingLoginAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        staffService.create("ExtraStaffLogin", "ExtraStaffPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.staffsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindFindStaffsMatchingLoginAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        staffService.create("ExtraStaffLogin", "ExtraStaffPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.staffsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfStaffs = response.getBody().as(new TypeRef<>() {});
        assertEquals(1, listOfStaffs.size());
    }

    @Test
    void staffControllerFindFindStaffsMatchingLoginAsAnAuthenticatedStaffTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        staffService.create("ExtraStaffLogin", "ExtraStaffPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.staffsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfStaffs = response.getBody().as(new TypeRef<>() {});
        assertEquals(1, listOfStaffs.size());
    }

    @Test
    void staffControllerFindFindStaffsAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.staffsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindFindStaffsAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.staffsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerFindFindStaffsAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.staffsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfStaffs = response.getBody().as(new TypeRef<>() {});
        assertEquals(2, listOfStaffs.size());
    }

    @Test
    void staffControllerFindFindStaffsAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.staffsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfStaffs = response.getBody().as(new TypeRef<>() {});
        assertEquals(2, listOfStaffs.size());
    }

    // Update tests

    @Test
    void staffControllerUpdateStaffAsAnUnauthenticatedUserTestNegative() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newStaffPassword = "SomeNewStaffPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newStaffPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerUpdateStaffAsAnAuthenticatedClientTestNegative() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to admin (not owner) account
        accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newStaffPassword = "SomeNewStaffPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newStaffPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerUpdateStaffAsAnAuthenticatedStaffThatIsOwnerOfTheAccountTestPositive() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to staff (owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String staffPasswordBefore = staffUserNo1.getPassword();
        String newStaffPassword = "SomeNewStaffPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newStaffPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Staff foundStaff = staffService.findByUUID(staffUserNo1.getId());

        String staffPasswordAfter = foundStaff.getPassword();

        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    void staffControllerUpdateStaffAsAnAuthenticatedStaffThatIsNotTheOwnerOfTheAccountTestPositive() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to staff (not owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUserNo2.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String newStaffPassword = "SomeNewStaffPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newStaffPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerUpdateStaffAsAnAuthenticatedAdminTestNegative() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to admin (not owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);
        
        String newStaffPassword = "SomeNewStaffPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newStaffPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerUpdateStaffWithoutIfMatchHeaderAsAnAuthenticatedStaffTestPositive() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to staff (owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);
        
        String newStaffPassword = "SomeNewStaffPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newStaffPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    void staffControllerUpdateStaffWithChangedIDAsAnAuthenticatedStaffTestPositive() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to staff (owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID newStaffID = UUID.randomUUID();

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(newStaffID)
            .login(accountResponse.getLogin())
            .password(passwordNotHashed)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void staffControllerUpdateStaffWithChangedLoginAsAnAuthenticatedStaffTestPositive() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to staff (owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String newStaffLogin = "SomeNewStaffLogin";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(newStaffLogin)
            .password(newStaffLogin)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void staffControllerUpdateStaffWithChangedStatusAsAnAuthenticatedStaffTestPositive() {
        // Login to staff (owner) account
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Get current user account by login
        String path = TestConstants.staffsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to staff (owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        boolean newStaffStatus = false;

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(passwordNotHashed)
            .active(newStaffStatus)
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.staffsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Activate tests

    @Test
    void staffControllerActivateStaffAsAnUnauthenticatedUserTestNegative() {
        UUID activatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerActivateStaffAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID activatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerActivateStaffAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID activatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerActivateStaffAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(204);

        Staff foundStaff = staffService.findByUUID(activatedStaffID);
        boolean staffStatusActiveAfter = foundStaff.isActive();

        assertTrue(staffStatusActiveAfter);
    }

    @Test
    void staffControllerActivateStaffThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedStaffID = UUID.randomUUID();
        String path = TestConstants.staffsURL + "/" + activatedStaffID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    void staffControllerDeactivateStaffAsAnUnauthenticatedUserTestNegative() {
        UUID deactivatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerDeactivateStaffAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID deactivatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerDeactivateStaffAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUserNo1.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID deactivatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void staffControllerDeactivateStaffAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedStaffID = staffUserNo1.getId();
        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(204);

        Staff foundStaff = staffService.findByUUID(deactivatedStaffID);
        boolean staffStatusActiveAfter = foundStaff.isActive();

        assertFalse(staffStatusActiveAfter);
    }

    @Test
    void staffControllerDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedStaffID = UUID.randomUUID();
        String path = TestConstants.staffsURL + "/" + deactivatedStaffID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
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
