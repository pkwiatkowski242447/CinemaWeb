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
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.service.impl.ClientServiceImpl;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.dto.account.LoginAccountRequest;
import pl.pas.gr3.cinema.dto.account.AccountResponse;
import pl.pas.gr3.cinema.dto.account.UpdateAccountRequest;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ClientControllerTest {

    private static AccountRepositoryImpl accountRepository;
    private static ClientServiceImpl clientService;
    private static PasswordEncoder passwordEncoder;

    private Client clientUserNo1;
    private Client clientUserNo2;
    private Staff staffUser;
    private Admin adminUser;
    private static String passwordNotHashed;

    @BeforeAll
    static void init() {
        accountRepository = new AccountRepositoryImpl(TestConstants.databaseName);
        clientService = new ClientServiceImpl(accountRepository);

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
        this.clearCollection();
        try {
            clientUserNo1 = accountRepository.createClient("ClientLoginX1", passwordEncoder.encode(passwordNotHashed));
            clientUserNo2 = accountRepository.createClient("ClientLoginX2", passwordEncoder.encode(passwordNotHashed));
            staffUser = accountRepository.createStaff("StaffLoginX", passwordEncoder.encode(passwordNotHashed));
            adminUser = accountRepository.createAdmin("AdminLoginX", passwordEncoder.encode(passwordNotHashed));
        } catch (Exception exception) {
            throw new RuntimeException("Could not create sample users with userRepository object.", exception);
        }
    }

    @AfterEach
    void destroySampleData() {
        this.clearCollection();
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
    void clientControllerFindClientByIDAsUnauthenticatedUserTestNegative() {
        UUID searchedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.get(path);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindClientByIDAsAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID searchedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindClientByIDAsAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientUserNo1.getId(), accountResponse.getId());
        assertEquals(clientUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(clientUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void clientControllerFindClientByIDAsAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientUserNo1.getId(), accountResponse.getId());
        assertEquals(clientUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(clientUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void clientControllerFindClientByIDThatIsNotInTheDatabaseAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID searchedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void clientControllerFindClientByIDThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID searchedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + searchedClientID;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void clientControllerFindClientByLoginTestAsAnUnauthenticatedUserTestNegative() {
        String searchedClientLogin = clientUserNo1.getLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindClientByLoginTestAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String searchedClientLogin = clientUserNo1.getLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindClientByLoginTestAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedClientLogin = clientUserNo1.getLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientUserNo1.getId(), accountResponse.getId());
        assertEquals(clientUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(clientUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void clientControllerFindClientByLoginTestAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedClientLogin = clientUserNo1.getLogin();
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        assertEquals(clientUserNo1.getId(), accountResponse.getId());
        assertEquals(clientUserNo1.getLogin(), accountResponse.getLogin());
        assertEquals(clientUserNo1.isActive(), accountResponse.isActive());
    }

    @Test
    void clientControllerFindClientByLoginThatIsNotInTheDatabaseAsAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String searchedClientLogin = "SomeNonExistentLogin";
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void clientControllerFindClientByLoginThatIsNotInTheDatabaseAsAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String searchedClientLogin = "SomeNonExistentLogin";
        String path = TestConstants.clientsURL + "/login/" + searchedClientLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(404);
    }

    @Test
    void clientControllerFindAllClientsMatchingLoginAsUnauthenticatedUserTestPositive() {
        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindAllClientsMatchingLoginAsAuthenticatedClientTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindAllClientsMatchingLoginAsAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfClients = response.getBody().as(new TypeRef<>() {});
        assertEquals(1, listOfClients.size());
    }

    @Test
    void clientControllerFindAllClientsMatchingLoginAsAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        clientService.create("ExtraClientLogin", "ExtraClientPassword");
        String matchedLogin = "Extra";
        String path = TestConstants.clientsURL + "?match=" + matchedLogin;

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfClients = response.getBody().as(new TypeRef<>() {});
        assertEquals(1, listOfClients.size());
    }

    @Test
    void clientControllerFindAllClientsAsAnUnauthenticatedUserTestNegative() {
        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindAllClientsAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerFindAllClientsAsAnAuthenticatedStaffTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfClients = response.getBody().as(new TypeRef<>() {});
        assertEquals(2, listOfClients.size());
    }

    @Test
    void clientControllerFindAllClientsAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        String path = TestConstants.clientsURL + "/all";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        List<AccountResponse> listOfClients = response.getBody().as(new TypeRef<>() {});
        assertEquals(2, listOfClients.size());
    }

    // Update tests

    @Test
    void clientControllerUpdateClientTestAsAnUnauthenticatedUserTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        String newClientPassword = "SomeNewClientPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newClientPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerUpdateClientTestAsAnAuthenticatedClientThatIsNotTheOwnerOfTheAccountTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo2.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newClientPassword = "SomeNewClientPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newClientPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerUpdateClientTestAsAnAuthenticatedClientThatIsOwnerOfTheAccountTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String clientPasswordBefore = clientUserNo1.getPassword();
        String newClientPassword = "SomeNewClientPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newClientPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(clientUserNo1.getId());

        String clientPasswordAfter = foundClient.getPassword();

        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    void clientControllerUpdateClientTestAsAnAuthenticatedStaffTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to staff account (not owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);
        
        String newClientPassword = "SomeNewClientPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newClientPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerUpdateClientTestAsAnAuthenticatedAdminTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to admin account (not owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);
        
        String newClientPassword = "SomeNewClientPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newClientPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerUpdateClientWithoutIfMatchHeaderAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);
        
        String newClientPassword = "SomeNewClientPasswordNo1";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(newClientPassword)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(412);
    }

    @Test
    void clientControllerUpdateClientWithChangedIDAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID newClientID = UUID.randomUUID();

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(newClientID)
            .login(accountResponse.getLogin())
            .password(passwordNotHashed)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void clientControllerUpdateClientWithChangedLoginAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        String newClientLogin = "SomeNewClientLogin";

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(newClientLogin)
            .password(passwordNotHashed)
            .active(accountResponse.isActive())
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    @Test
    void clientControllerUpdateClientWithChangedStatusAsAnAuthenticatedClientTestNegative() {
        // Login to client owner account
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        // Get current user account by login
        String path = TestConstants.clientsURL + "/login/self";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.accept(ContentType.JSON);

        Response response = requestSpecification.get(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        AccountResponse accountResponse = response.getBody().as(AccountResponse.class);
        String etagContent = response.header(HttpHeaders.ETAG);

        // Login to client account (owner)
        accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        boolean newClientStatus = false;

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
            .id(accountResponse.getId())
            .login(accountResponse.getLogin())
            .password(passwordNotHashed)
            .active(newClientStatus)
            .build();

        requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        requestSpecification.header(HttpHeaders.IF_MATCH, etagContent);
        requestSpecification.body(updateAccountRequest);

        response = requestSpecification.put(TestConstants.clientsURL + "/update");
        validatableResponse = response.then();
        validatableResponse.statusCode(400);
    }

    // Activate tests

    @Test
    void clientControllerActivateClientAsAnUnauthenticatedUserTestNegative() {
        UUID activatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerActivateClientAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID activatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerActivateClientAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID activatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerActivateClientAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(activatedClientID);
        boolean clientStatusActiveAfter = foundClient.isActive();

        assertTrue(clientStatusActiveAfter);
    }

    @Test
    void clientControllerActivateClientThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID activatedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + activatedClientID + "/activate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    // Deactivate tests

    @Test
    void clientControllerDeactivateClientAsAnUnauthenticatedClientTestNegative() {
        UUID deactivatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerDeactivateClientAsAnAuthenticatedClientTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(clientUserNo1.getLogin(), passwordNotHashed), TestConstants.clientLoginURL);

        UUID deactivatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerDeactivateClientAsAnAuthenticatedStaffTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(staffUser.getLogin(), passwordNotHashed), TestConstants.staffLoginURL);

        UUID deactivatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(403);
    }

    @Test
    void clientControllerDeactivateClientAsAnAuthenticatedAdminTestPositive() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedClientID = clientUserNo1.getId();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(204);

        Client foundClient = clientService.findByUUID(deactivatedClientID);
        boolean clientStatusActiveAfter = foundClient.isActive();

        assertFalse(clientStatusActiveAfter);
    }

    @Test
    void clientControllerDeactivateClientThatIsNotInTheDatabaseAsAnAuthenticatedAdminTestNegative() {
        String accessToken = this.loginToAccount(new LoginAccountRequest(adminUser.getLogin(), passwordNotHashed), TestConstants.adminLoginURL);

        UUID deactivatedClientID = UUID.randomUUID();
        String path = TestConstants.clientsURL + "/" + deactivatedClientID + "/deactivate";

        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        Response response = requestSpecification.post(path);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(400);
    }

    private String loginToAccount(LoginAccountRequest loginRequest, String loginURL) {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body(new LoginAccountRequest(loginRequest.login(), loginRequest.password()));

        Response response = requestSpecification.post(loginURL);
        ValidatableResponse validatableResponse = response.then();

        validatableResponse.statusCode(200);

        return response.getBody().asString();
    }
}
