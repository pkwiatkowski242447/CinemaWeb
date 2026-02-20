package pl.pas.gr3.cinema.api;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
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
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.dto.auth.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.dto.auth.UpdateAccountRequest;
import pl.pas.gr3.cinema.service.impl.AdminServiceImpl;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AdminControllerTest {

    private static AccountRepositoryImpl accountRepository;
    private static AdminServiceImpl adminService;
    private static PasswordEncoder passwordEncoder;

    private Client clientUser;
    private Staff staffUser;
    private Admin adminUserNo1;
    private Admin adminUserNo2;
    private static String passwordNotHashed;

    @BeforeAll
    static void init() {
        accountRepository = new AccountRepositoryImpl(TestConstants.databaseName);
        adminService = new AdminServiceImpl(accountRepository);

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
            adminUserNo1 = accountRepository.createAdmin("AdminLoginX1", passwordEncoder.encode(passwordNotHashed));
            adminUserNo2 = accountRepository.createAdmin("AdminLoginX2", passwordEncoder.encode(passwordNotHashed));
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

    @AfterAll
    static void destroy() {
        accountRepository.close();
    }

    // Read tests

    @Test
    void adminControllerFindAdminByIDAsUnauthenticatedUserTestNegative() {
        UUID searchedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAdminByIDAsAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAdminByIDAsAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAdminByIDAsAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(adminUserNo1.getId(), accountResponse.id());
        assertEquals(adminUserNo1.getLogin(), accountResponse.login());
        assertEquals(adminUserNo1.isActive(), accountResponse.active());
    }

    @Test
    void adminControllerFindAdminByIDThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedAdminID = UUID.randomUUID();
        String path = TestConstants.adminsURL + "/" + searchedAdminID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void adminControllerFindAdminByLoginAsUnauthenticatedUserTestNegative() {
        String searchedAdminLogin = adminUserNo1.getLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAdminByLoginAsAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String searchedAdminLogin = adminUserNo1.getLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAdminByLoginAsAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedAdminLogin = adminUserNo1.getLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAdminByLoginAsAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedAdminLogin = adminUserNo1.getLogin();
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        assertNotNull(response.asString());

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(adminUserNo1.getId(), accountResponse.id());
        assertEquals(adminUserNo1.getLogin(), accountResponse.login());
        assertEquals(adminUserNo1.isActive(), accountResponse.active());
    }

    @Test
    void adminControllerFindAdminByLoginThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedAdminLogin = "SomeNonExistentLogin";
        String path = TestConstants.adminsURL + "/login/" + searchedAdminLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        assertNotNull(response.asString());

        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(404);
    }

    // Current

    @Test
    void adminControllerFindAllAdminsMatchingLoginAsUnauthenticatedUserTestNegative() {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAllAdminsMatchingLoginAsAuthenticatedClientTestNegative() {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAllAdminsMatchingLoginAsAuthenticatedStaffTestNegative() {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAllAdminsMatchingLoginAsAuthenticatedAdminTestPositive() {
        adminService.create("ExtraAdminLogin", "ExtraAdminPassword");

        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String matchedLogin = "Extra";
        String path = TestConstants.adminsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfAdmins = response.getBody().as(new TypeRef<>() {});
        assertEquals(1, listOfAdmins.size());
    }

    @Test
    void adminControllerFindAllAdminsAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAllAdminsAsAnAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAllAdminsAsAnAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerFindAllAdminsAsAnAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.adminsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfAdmins = response.getBody().as(new TypeRef<>() {});
        assertEquals(2, listOfAdmins.size());
    }

    // Update tests

    @Test
    void adminControllerUpdateAdminAsUnauthenticatedUserTestNegative() {
        // Login with admin (owner) credentials
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Get current user account by login
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        assertTrue(response.asString().isEmpty());
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerUpdateAdminAsAuthenticatedClientTestNegative() {
        // Login with admin (owner) credentials
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Get current user account by login
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to staff (not owner) account
        accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerUpdateAdminAsAuthenticatedStaffTestNegative() {
        // Login with admin (owner) credentials
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Get current user account by login
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(200);

        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to staff (not owner) account
        accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerUpdateAdminAsAuthenticatedAdminThatIsNotAccountOwnerTestPositive() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse userOutputDTO = response.getBody().as(AccountResponse.class);

        // Login to admin (not owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo2.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(userOutputDTO.id(), userOutputDTO.login(), newAdminPassword, userOutputDTO.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerUpdateAdminAsAuthenticatedAdminThatIsAccountOwnerTestPositive() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String adminPasswordBefore = adminUserNo1.getPassword();
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        assertTrue(response.asString().isEmpty());
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Admin foundAdmin = adminService.findByUUID(adminUserNo1.getId());

        String adminPasswordAfter = foundAdmin.getPassword();

        assertNotEquals(adminPasswordBefore, passwordEncoder.encode(adminPasswordAfter));
    }

    @Test
    void adminControllerUpdateAdminAsAuthenticatedAdminThatIsAccountOwnerWithoutIfMatchTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = "SomeNewAdminPasswordNo1";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    void adminControllerUpdateAdminWithChangedIdentifierAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        UUID newAdminId = UUID.randomUUID();

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(newAdminId, accountResponse.login(), adminUserNo1.getPassword(), accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void adminControllerUpdateAdminWithChangedLoginAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminLogin = "SomeOtherAdminLogin";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), newAdminLogin, adminUserNo1.getPassword(), accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void adminControllerUpdateAdminWithChangedStatusAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        boolean newAdminStatusActive = false;

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), adminUserNo1.getPassword(), newAdminStatusActive);
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void adminControllerUpdateAdminWithNullPasswordAsAuthenticatedAdminTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = null;

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerUpdateAdminWithEmptyPasswordTestNegative() {
        // Login with admin (owner) credentials
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed));

        Response response = requestSpecification.post(TestConstants.adminLoginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);
        String accessToken = response.getBody().asString();

        // Get current user account by login
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        response = requestSpecification.get(TestConstants.adminsURL + "/login/self");
        String eTag = response.getHeader(HttpHeaders.ETAG);
        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to admin (account owner) account
        accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        // Prepare user update
        String newAdminPassword = "";

        UpdateAccountRequest userUpdateDTO = new UpdateAccountRequest(accountResponse.id(), accountResponse.login(), newAdminPassword, accountResponse.active());
        requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header("If-Match", eTag);
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.body(userUpdateDTO);

        response = requestSpecification.put(TestConstants.adminsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    // Activate tests

    @Test
    void adminControllerActivateAdminAsUnauthenticatedUserTestNegative() {
        UUID activatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerActivateAdminAsAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID activatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerActivateAdminAsAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID activatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerActivateAdminAsAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Admin foundAdmin = adminService.findByUUID(activatedAdminID);
        boolean adminStatusActiveAfter = foundAdmin.isActive();

        assertTrue(adminStatusActiveAfter);
    }

    @Test
    void adminControllerActivateAdminThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedAdminID = UUID.randomUUID();
        String path = TestConstants.adminsURL + "/" + activatedAdminID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    void adminControllerDeactivateAdminAsUnauthenticatedUserTestNegative() {
        UUID deactivatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerDeactivateAdminAsAuthenticatedClientTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(clientUser.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID deactivatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerDeactivateAdminAsAuthenticatedStaffTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID deactivatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void adminControllerDeactivateAdminAsAuthenticatedAdminTestPositive() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        boolean adminStatusActiveBefore = adminUserNo1.isActive();
        UUID deactivatedAdminID = adminUserNo1.getId();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Admin foundAdmin = adminService.findByUUID(deactivatedAdminID);
        boolean adminStatusActiveAfter = foundAdmin.isActive();

        assertFalse(adminStatusActiveAfter);
        assertTrue(adminStatusActiveBefore);
    }

    @Test
    void adminControllerDeactivateAdminThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = loginToAccount(new LoginAccountRequest(adminUserNo1.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedAdminID = UUID.randomUUID();
        String path = TestConstants.adminsURL + "/" + deactivatedAdminID + "/deactivate";

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
