package repositories;

import org.junit.jupiter.api.*;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {

    private final static String databaseName = "test";

    private static ClientRepository clientRepositoryForTests;

    private Client clientNo1;
    private Client clientNo2;
    private Admin adminNo1;
    private Admin adminNo2;
    private Staff staffNo1;
    private Staff staffNo2;

    @BeforeAll
    public static void init() {
        clientRepositoryForTests = new ClientRepository(databaseName);
    }

    @BeforeEach
    public void addExampleClients() {
        try {
            clientNo1 = clientRepositoryForTests.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = clientRepositoryForTests.createClient("ClientLoginNo2", "ClientPasswordNo2");
            adminNo1 = clientRepositoryForTests.createAdmin("AdminLoginNo1", "AdminPasswordNo1");
            adminNo2 = clientRepositoryForTests.createAdmin("AdminLoginNo2", "AdminPasswordNo2");
            staffNo1 = clientRepositoryForTests.createStaff("StaffLoginNo1", "StaffPasswordNo1");
            staffNo2 = clientRepositoryForTests.createStaff("StaffLoginNo2", "StaffPasswordNo2");
        } catch (ClientRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding clients to it.", exception);
        }
    }

    @AfterEach
    public void removeExampleClients() {
        try {
            List<Client> listOfAllClients = clientRepositoryForTests.findAll();
            for (Client client : listOfAllClients) {
                clientRepositoryForTests.delete(client.getClientID());
            }
        } catch (ClientRepositoryException exception) {
            throw new RuntimeException("Could not remove all clients from the test database after client repository tests.", exception);
        }
    }

    @AfterAll
    public static void destroy() {
        clientRepositoryForTests.close();
    }

    @Test
    public void clientRepositoryCreateClientTestPositive() throws ClientRepositoryException {
        Client client = clientRepositoryForTests.createClient("SomeLogin", "SomePassword");
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void mongoRepositoryFindClientWithClientID() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertEquals(clientNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Client.class);
    }

    @Test
    public void mongoRepositoryFindAdminWithAdminID() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(adminNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(adminNo1, foundClient);
        assertEquals(adminNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Admin.class);
    }

    @Test
    public void mongoRepositoryFindStaffWithStaffID() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(staffNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(staffNo1, foundClient);
        assertEquals(staffNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Staff.class);
    }

    @Test
    public void clientRepositoryCreateClientWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient(clientNo1.getClientLogin(), "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateClientWithNullLoginTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient(null, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateClientWithLoginTooShortTestNegative() {
        String clientLogin = "dddf";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateClientWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String clientLogin = "dddfdddf";
        Client client = clientRepositoryForTests.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String clientLogin = "dddfdddfdddfdddfdddf";
        Client client = clientRepositoryForTests.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateClientWithNullPasswordTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient("SomeLogin", null));
    }

    @Test
    public void clientRepositoryCreateClientWithEmptyPasswordTestNegative() {
        String clientPassword = "";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient("SomeLogin", clientPassword));
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordTooShortTestNegative() {
        String clientPassword = "dddf";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient("SomeLogin", clientPassword));
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordTooLongTestNegative() {
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient("SomeLogin", clientPassword));
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String clientPassword = "dddfdddf";
        Client client = clientRepositoryForTests.createClient("SomeLogin", clientPassword);
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String clientPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        Client client = clientRepositoryForTests.createClient("SomeLogin", clientPassword);
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordThatViolatesRegExTestNegative() {
        String clientPassword = "Some Password";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient("SomeLogin", clientPassword));
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin(adminNo1.getClientLogin(), "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateAdminWithNullLoginTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin(null, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "dddf";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String adminLogin = "dddfdddf";
        Client admin = clientRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Client foundAdmin = clientRepositoryForTests.findByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String adminLogin = "dddfdddfdddfdddfdddf";
        Client admin = clientRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Client foundAdmin = clientRepositoryForTests.findByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Login";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateAdminWithNullPasswordTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin("SomeLogin", null));
    }

    @Test
    public void clientRepositoryCreateAdminWithEmptyPasswordTestNegative() {
        String adminPassword = "";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin("SomeLogin", adminPassword));
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordTooShortTestNegative() {
        String adminPassword = "dddf";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin("SomeLogin", adminPassword));
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordTooLongTestNegative() {
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin("SomeLogin", adminPassword));
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String adminPassword = "dddfdddf";
        Client admin = clientRepositoryForTests.createAdmin("SomeLogin", adminPassword);
        assertNotNull(admin);
        Client foundAdmin = clientRepositoryForTests.findByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String adminPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        Client admin = clientRepositoryForTests.createAdmin("SomeLogin", adminPassword);
        assertNotNull(admin);
        Client foundAdmin = clientRepositoryForTests.findByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordThatViolatesRegExTestNegative() {
        String adminPassword = "Some Password";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin("SomeLogin", adminPassword));
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff(staffNo1.getClientLogin(), "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateStaffWithNullLoginTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff(null, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "dddf";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String staffLogin = "dddfdddf";
        Client staff = clientRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Client foundStaff = clientRepositoryForTests.findByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String staffLogin = "dddfdddfdddfdddfdddf";
        Client staff = clientRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Client foundStaff = clientRepositoryForTests.findByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Login";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    public void clientRepositoryCreateStaffWithNullPasswordTestNegative() {
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff("SomeLogin", null));
    }

    @Test
    public void clientRepositoryCreateStaffWithEmptyPasswordTestNegative() {
        String staffPassword = "";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff("SomeLogin", staffPassword));
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordTooShortTestNegative() {
        String staffPassword = "dddf";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff("SomeLogin", staffPassword));
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordTooLongTestNegative() {
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff("SomeLogin", staffPassword));
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String staffPassword = "dddfdddf";
        Client staff = clientRepositoryForTests.createStaff("SomeLogin", staffPassword);
        assertNotNull(staff);
        Client foundStaff = clientRepositoryForTests.findByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String staffPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        Client staff = clientRepositoryForTests.createStaff("SomeLogin", staffPassword);
        assertNotNull(staff);
        Client foundStaff = clientRepositoryForTests.findByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordThatViolatesRegExTestNegative() {
        String staffPassword = "Some Password";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff("SomeLogin", staffPassword));
    }

    @Test
    public void clientRepositoryFindClientTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientRepositoryFindClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByUUID(client.getClientID()));
    }

    @Test
    public void clientRepositoryFindAllClientUUIDsTestPositive() throws ClientRepositoryException {
        List<UUID> listOfAllUUIDs = clientRepositoryForTests.findAllUUIDs();
        assertNotNull(listOfAllUUIDs);
        assertFalse(listOfAllUUIDs.isEmpty());
        assertEquals(6, listOfAllUUIDs.size());
    }

    @Test
    public void clientRepositoryFindAllClientsTestPositive() throws ClientRepositoryException {
        List<Client> listOfAllClients = clientRepositoryForTests.findAll();
        assertNotNull(listOfAllClients);
        assertFalse(listOfAllClients.isEmpty());
        assertEquals(6, listOfAllClients.size());
    }

    @Test
    public void clientRepositoryFindClientByLoginTestPositive() throws ClientRepositoryException {
        String clientLogin = clientNo1.getClientLogin();
        Client foundClient = clientRepositoryForTests.findByLogin(clientLogin);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientRepositoryFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByLogin(client.getClientLogin()));
    }

    @Test
    public void clientRepositoryFindAllClientsMatchingLoginTestPositive() throws ClientRepositoryException {
        String clientLogin = "Client";
        List<Client> listOfFoundClients = clientRepositoryForTests.findAllMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertFalse(listOfFoundClients.isEmpty());
        assertEquals(2, listOfFoundClients.size());
    }

    @Test
    public void clientRepositoryFindAllClientsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws ClientRepositoryException {
        String clientLogin = "NonExistentLogin";
        List<Client> listOfFoundClients = clientRepositoryForTests.findAllMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertTrue(listOfFoundClients.isEmpty());
    }

    @Test
    public void clientRepositoryActivateClientTestPositive() throws ClientRepositoryException {
        clientNo1.setClientStatusActive(false);
        clientRepositoryForTests.update(clientNo1);
        Client foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
        clientRepositoryForTests.activate(clientNo1);
        foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isClientStatusActive());
    }

    @Test
    public void clientRepositoryActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientActivationException.class, () -> clientRepositoryForTests.activate(client));
    }

    @Test
    public void clientRepositoryDeactivateClientTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isClientStatusActive());
        clientRepositoryForTests.deactivate(clientNo1);
        foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
    }

    @Test
    public void clientRepositoryDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientDeactivationException.class, () -> clientRepositoryForTests.deactivate(client));
    }

    @Test
    public void clientRepositoryUpdateClientTestPositive() throws ClientRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = clientNo1.getClientLogin();
        String passwordBefore = clientNo1.getClientPassword();
        clientNo1.setClientLogin(newLogin);
        clientNo1.setClientPassword(newPassword);
        clientRepositoryForTests.update(clientNo1);
        String loginAfter = clientNo1.getClientLogin();
        String passwordAfter = clientNo1.getClientPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithNullLoginTestNegative() {
        clientNo1.setClientLogin(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithEmptyLoginTestNegative() {
        String newLogin = "";
        clientNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        clientNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        clientNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = clientNo1.getClientLogin();
        clientNo1.setClientLogin(newLogin);
        clientRepositoryForTests.update(clientNo1);
        String loginAfter = clientNo1.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = clientNo1.getClientLogin();
        clientNo1.setClientLogin(newLogin);
        clientRepositoryForTests.update(clientNo1);
        String loginAfter = clientNo1.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        clientNo1.setClientLogin(clientLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithNullPasswordTestNegative() {
        clientNo1.setClientPassword(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithEmptyPasswordTestNegative() {
        String newPassword = "";
        clientNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordTooShortTestNegative() {
        String newPassword = "dddf";
        clientNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordTooLongTestNegative() {
        String newPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddf";
        String passwordBefore = clientNo1.getClientPassword();
        clientNo1.setClientPassword(newPassword);
        clientRepositoryForTests.update(clientNo1);
        String passwordAfter = clientNo1.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        String passwordBefore = clientNo1.getClientPassword();
        clientNo1.setClientPassword(newPassword);
        clientRepositoryForTests.update(clientNo1);
        String passwordAfter = clientNo1.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordThatViolatesRegExTestNegative() {
        String clientPassword = "Some Password";
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.update(clientNo1));
    }

    @Test
    public void clientRepositoryDeleteClientTestPositive() throws ClientRepositoryException {
        int numOfClientsBefore = clientRepositoryForTests.findAll().size();
        UUID removedClientID = clientNo1.getClientID();
        clientRepositoryForTests.delete(clientNo1.getClientID());
        int numOfClientsAfter = clientRepositoryForTests.findAll().size();
        assertNotEquals(numOfClientsBefore, numOfClientsAfter);
        assertEquals(6, numOfClientsBefore);
        assertEquals(5, numOfClientsAfter);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByUUID(removedClientID));
    }

    @Test
    public void clientRepositoryDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientRepositoryDeleteException.class, () -> clientRepositoryForTests.delete(client.getClientID()));
    }
}
