package pl.pas.gr3.cinema.repositories;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.*;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientActivationException;
import pl.pas.gr3.cinema.exceptions.repositories.other.client.ClientDeactivationException;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {

    private final static String databaseName = "test";
    private final static Logger logger = LoggerFactory.getLogger(ClientRepositoryTest.class);
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
            List<Client> listOfAllClients = clientRepositoryForTests.findAllClients();
            for (Client client : listOfAllClients) {
                clientRepositoryForTests.delete(client.getClientID(), "client");
            }

            List<Admin> listOfAllAdmins = clientRepositoryForTests.findAllAdmins();
            for (Admin admin : listOfAllAdmins) {
                clientRepositoryForTests.delete(admin.getClientID(), "admin");
            }

            List<Staff> listOfAllStaffs = clientRepositoryForTests.findAllStaffs();
            for (Staff staff : listOfAllStaffs) {
                clientRepositoryForTests.delete(staff.getClientID(), "staff");
            }
        } catch (ClientRepositoryException exception) {
            throw new RuntimeException("Could not remove all clients from the test database after client repository tests.", exception);
        }
    }

    @AfterAll
    public static void destroy() {
        clientRepositoryForTests.close();
    }

    // Some mongo tests

    @Test
    public void mongoRepositoryFindClientWithClientIDTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertEquals(clientNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Client.class);
    }

    @Test
    public void mongoRepositoryFindClientThatIsNotInTheDatabaseWithClientIDTestNegative() {
        Client client = new Client(UUID.randomUUID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
        assertNotNull(client);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByUUID(client.getClientID()));
    }

    @Test
    public void mongoRepositoryFindAdminWithAdminIDTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(adminNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(adminNo1, foundClient);
        assertEquals(adminNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Admin.class);
    }

    @Test
    public void mongoRepositoryFindAdminWithAdminThatIsNotInTheDatabaseIDTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
        assertNotNull(admin);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByUUID(admin.getClientID()));
    }

    @Test
    public void mongoRepositoryFindStaffWithStaffIDTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findByUUID(staffNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(staffNo1, foundClient);
        assertEquals(staffNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Staff.class);
    }

    @Test
    public void mongoRepositoryFindStaffThatIsNotInTheDatabaseWithStaffIDTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
        assertNotNull(staff);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByUUID(staff.getClientID()));
    }

    // Client create tests

    @Test
    public void clientRepositoryCreateClientTestPositive() throws ClientRepositoryException {
        Client client = clientRepositoryForTests.createClient("SomeLogin", "SomePassword");
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findClientByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(ClientRepositoryCreateClientDuplicateLoginException.class, () -> clientRepositoryForTests.createClient(clientNo1.getClientLogin(), "SomePassword"));
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
        Client foundClient = clientRepositoryForTests.findClientByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String clientLogin = "dddfdddfdddfdddfdddf";
        Client client = clientRepositoryForTests.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findClientByUUID(client.getClientID());
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
        Client foundClient = clientRepositoryForTests.findClientByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String clientPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        Client client = clientRepositoryForTests.createClient("SomeLogin", clientPassword);
        assertNotNull(client);
        Client foundClient = clientRepositoryForTests.findClientByUUID(client.getClientID());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    public void clientRepositoryCreateClientWithPasswordThatViolatesRegExTestNegative() {
        String clientPassword = "Some Password";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createClient("SomeLogin", clientPassword));
    }

    // Admin create tests

    @Test
    public void clientRepositoryCreateAdminTestPositive() throws ClientRepositoryException {
        Admin admin = clientRepositoryForTests.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(ClientRepositoryCreateClientDuplicateLoginException.class, () -> clientRepositoryForTests.createAdmin(adminNo1.getClientLogin(), "SomePassword"));
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
        Admin admin = clientRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String adminLogin = "dddfdddfdddfdddfdddf";
        Admin admin = clientRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(admin.getClientID());
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
        Admin admin = clientRepositoryForTests.createAdmin("SomeLogin", adminPassword);
        assertNotNull(admin);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String adminPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        Admin admin = clientRepositoryForTests.createAdmin("SomeLogin", adminPassword);
        assertNotNull(admin);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(admin.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    public void clientRepositoryCreateAdminWithPasswordThatViolatesRegExTestNegative() {
        String adminPassword = "Some Password";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createAdmin("SomeLogin", adminPassword));
    }

    // Staff create tests

    @Test
    public void clientRepositoryCreateStaffTestPositive() throws ClientRepositoryException {
        Staff staff = clientRepositoryForTests.createStaff("SomeLogin", "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(ClientRepositoryCreateClientDuplicateLoginException.class, () -> clientRepositoryForTests.createStaff(staffNo1.getClientLogin(), "SomePassword"));
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
        Staff staff = clientRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String staffLogin = "dddfdddfdddfdddfdddf";
        Staff staff = clientRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staff.getClientID());
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
        Staff staff = clientRepositoryForTests.createStaff("SomeLogin", staffPassword);
        assertNotNull(staff);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String staffPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        Staff staff = clientRepositoryForTests.createStaff("SomeLogin", staffPassword);
        assertNotNull(staff);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staff.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    public void clientRepositoryCreateStaffWithPasswordThatViolatesRegExTestNegative() {
        String staffPassword = "Some Password";
        assertThrows(ClientRepositoryCreateException.class, () -> clientRepositoryForTests.createStaff("SomeLogin", staffPassword));
    }

    // Find client by ID tests

    @Test
    public void clientRepositoryFindClientByIDTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientRepositoryFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(ClientRepositoryClientNotFoundException.class, () -> clientRepositoryForTests.findClientByUUID(client.getClientID()));
    }

    @Test
    public void clientRepositoryFindAdminByIDTestPositive() throws ClientRepositoryException {
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void clientRepositoryFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(ClientRepositoryAdminNotFoundException.class, () -> clientRepositoryForTests.findAdminByUUID(admin.getClientID()));
    }

    @Test
    public void clientRepositoryFindStaffByIDTestPositive() throws ClientRepositoryException {
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void clientRepositoryFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(ClientRepositoryStaffNotFoundException.class, () -> clientRepositoryForTests.findStaffByUUID(staff.getClientID()));
    }

    // Find all users

    @Test
    public void clientRepositoryFindAllClientsTestPositive() throws ClientRepositoryException {
        List<Client> listOfAllClients = clientRepositoryForTests.findAllClients();
        assertNotNull(listOfAllClients);
        assertFalse(listOfAllClients.isEmpty());
        assertEquals(2, listOfAllClients.size());
    }

    @Test
    public void clientRepositoryFindAllAdminsTestPositive() throws ClientRepositoryException {
        List<Admin> listOfAllAdmins = clientRepositoryForTests.findAllAdmins();
        assertNotNull(listOfAllAdmins);
        assertFalse(listOfAllAdmins.isEmpty());
        assertEquals(2, listOfAllAdmins.size());
    }

    @Test
    public void clientRepositoryFindAllStaffsTestPositive() throws ClientRepositoryException {
        List<Staff> listOfAllStaffs = clientRepositoryForTests.findAllStaffs();
        assertNotNull(listOfAllStaffs);
        assertFalse(listOfAllStaffs.isEmpty());
        assertEquals(2, listOfAllStaffs.size());
    }

    // Find users by logins

    @Test
    public void clientRepositoryFindClientByLoginTestPositive() throws ClientRepositoryException {
        String clientLogin = clientNo1.getClientLogin();
        Client foundClient = clientRepositoryForTests.findClientByLogin(clientLogin);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientRepositoryFindAdminByLoginTestPositive() throws ClientRepositoryException {
        String adminLogin = adminNo1.getClientLogin();
        Client foundAdmin = clientRepositoryForTests.findAdminByLogin(adminLogin);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void clientRepositoryFindStaffByLoginTestPositive() throws ClientRepositoryException {
        String staffLogin = staffNo1.getClientLogin();
        Client foundStaff = clientRepositoryForTests.findStaffByLogin(staffLogin);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    // Find users by login that is not in the database

    @Test
    public void clientRepositoryFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(ClientRepositoryClientNotFoundException.class, () -> clientRepositoryForTests.findClientByLogin(client.getClientLogin()));
    }

    @Test
    public void clientRepositoryFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(ClientRepositoryAdminNotFoundException.class, () -> clientRepositoryForTests.findAdminByLogin(admin.getClientLogin()));
    }

    @Test
    public void clientRepositoryFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(ClientRepositoryStaffNotFoundException.class, () -> clientRepositoryForTests.findStaffByLogin(staff.getClientLogin()));
    }

    // Find all users matching login

    @Test
    public void clientRepositoryFindAllClientsMatchingLoginTestPositive() throws ClientRepositoryException {
        String clientLogin = "Client";
        List<Client> listOfFoundClients = clientRepositoryForTests.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertFalse(listOfFoundClients.isEmpty());
        assertEquals(2, listOfFoundClients.size());
    }

    @Test
    public void clientRepositoryFindAllClientsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws ClientRepositoryException {
        String clientLogin = "NonExistentLogin";
        List<Client> listOfFoundClients = clientRepositoryForTests.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertTrue(listOfFoundClients.isEmpty());
    }

    @Test
    public void clientRepositoryFindAllAdminsMatchingLoginTestPositive() throws ClientRepositoryException {
        String adminLogin = "Admin";
        List<Admin> listOfFoundAdmins = clientRepositoryForTests.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertFalse(listOfFoundAdmins.isEmpty());
        assertEquals(2, listOfFoundAdmins.size());
    }

    @Test
    public void clientRepositoryFindAllAdminsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws ClientRepositoryException {
        String adminLogin = "NonExistentLogin";
        List<Admin> listOfFoundAdmins = clientRepositoryForTests.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertTrue(listOfFoundAdmins.isEmpty());
    }

    @Test
    public void clientRepositoryFindAllStaffsMatchingLoginTestPositive() throws ClientRepositoryException {
        String staffLogin = "Staff";
        List<Staff> listOfFoundStaffs = clientRepositoryForTests.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertFalse(listOfFoundStaffs.isEmpty());
        assertEquals(2, listOfFoundStaffs.size());
    }

    @Test
    public void clientRepositoryFindAllStaffsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws ClientRepositoryException {
        String staffLogin = "NonExistentLogin";
        List<Staff> listOfFoundStaffs = clientRepositoryForTests.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertTrue(listOfFoundStaffs.isEmpty());
    }

    // Find all clients UUIDs

    @Test
    public void clientRepositoryFindAllClientUUIDsTestPositive() throws ClientRepositoryException {
        List<UUID> listOfAllUUIDs = clientRepositoryForTests.findAllUUIDs();
        assertNotNull(listOfAllUUIDs);
        assertFalse(listOfAllUUIDs.isEmpty());
        assertEquals(6, listOfAllUUIDs.size());
    }

    // Activate users tests positive

    @Test
    public void clientRepositoryActivateClientTestPositive() throws ClientRepositoryException {
        clientNo1.setClientStatusActive(false);
        clientRepositoryForTests.updateClient(clientNo1);
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
        clientRepositoryForTests.activate(clientNo1, "client");
        foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isClientStatusActive());
    }

    @Test
    public void clientRepositoryActivateAdminTestPositive() throws ClientRepositoryException {
        adminNo1.setClientStatusActive(false);
        clientRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isClientStatusActive());
        clientRepositoryForTests.activate(adminNo1, "admin");
        foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isClientStatusActive());
    }

    @Test
    public void clientRepositoryActivateStaffTestPositive() throws ClientRepositoryException {
        staffNo1.setClientStatusActive(false);
        clientRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isClientStatusActive());
        clientRepositoryForTests.activate(staffNo1, "staff");
        foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isClientStatusActive());
    }

    // Deactivate users test negative

    @Test
    public void clientRepositoryActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientActivationException.class, () -> clientRepositoryForTests.activate(client, "client"));
    }

    @Test
    public void clientRepositoryActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientActivationException.class, () -> clientRepositoryForTests.activate(admin, "admin"));
    }

    @Test
    public void clientRepositoryActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientActivationException.class, () -> clientRepositoryForTests.activate(staff, "staff"));
    }

    // Deactivate users tests positive

    @Test
    public void clientRepositoryDeactivateClientTestPositive() throws ClientRepositoryException {
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isClientStatusActive());
        clientRepositoryForTests.deactivate(clientNo1, "client");
        foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
    }

    @Test
    public void clientRepositoryDeactivateAdminTestPositive() throws ClientRepositoryException {
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isClientStatusActive());
        clientRepositoryForTests.deactivate(adminNo1, "admin");
        foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isClientStatusActive());
    }

    @Test
    public void clientRepositoryDeactivateStaffTestPositive() throws ClientRepositoryException {
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isClientStatusActive());
        clientRepositoryForTests.deactivate(staffNo1, "staff");
        foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isClientStatusActive());
    }

    // Deactivate users test negative

    @Test
    public void clientRepositoryDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientDeactivationException.class, () -> clientRepositoryForTests.deactivate(client, "client"));
    }

    @Test
    public void clientRepositoryDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientDeactivationException.class, () -> clientRepositoryForTests.deactivate(admin, "admin"));
    }

    @Test
    public void clientRepositoryDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientDeactivationException.class, () -> clientRepositoryForTests.deactivate(staff, "staff"));
    }

    // Update client tests

    @Test
    public void clientRepositoryUpdateClientTestPositive() throws ClientRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = clientNo1.getClientLogin();
        String passwordBefore = clientNo1.getClientPassword();
        clientNo1.setClientLogin(newLogin);
        clientNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateClient(clientNo1);
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        String loginAfter = foundClient.getClientLogin();
        String passwordAfter = foundClient.getClientPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), clientNo1.getClientLogin(), clientNo1.getClientPassword(), clientNo1.isClientStatusActive());
        assertNotNull(client);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(client));
    }

    @Test
    public void clientRepositoryUpdateClientWithNullLoginTestNegative() {
        clientNo1.setClientLogin(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithEmptyLoginTestNegative() {
        String newLogin = "";
        clientNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        clientNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        clientNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = clientNo1.getClientLogin();
        clientNo1.setClientLogin(newLogin);
        clientRepositoryForTests.updateClient(clientNo1);
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        String loginAfter = foundClient.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = clientNo1.getClientLogin();
        clientNo1.setClientLogin(newLogin);
        clientRepositoryForTests.updateClient(clientNo1);
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        String loginAfter = foundClient.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        clientNo1.setClientLogin(clientLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithNullPasswordTestNegative() {
        clientNo1.setClientPassword(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithEmptyPasswordTestNegative() {
        String newPassword = "";
        clientNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordTooShortTestNegative() {
        String newPassword = "dddf";
        clientNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordTooLongTestNegative() {
        String newPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddf";
        String passwordBefore = clientNo1.getClientPassword();
        clientNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateClient(clientNo1);
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        String passwordAfter = foundClient.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        String passwordBefore = clientNo1.getClientPassword();
        clientNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateClient(clientNo1);
        Client foundClient = clientRepositoryForTests.findClientByUUID(clientNo1.getClientID());
        String passwordAfter = foundClient.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateClientWithPasswordThatViolatesRegExTestNegative() {
        String clientPassword = "Some Password";
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(clientNo1));
    }

    // Update admin tests

    @Test
    public void clientRepositoryUpdateAdminTestPositive() throws ClientRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = adminNo1.getClientLogin();
        String passwordBefore = adminNo1.getClientPassword();
        adminNo1.setClientLogin(newLogin);
        adminNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        String loginAfter = foundAdmin.getClientLogin();
        String passwordAfter = foundAdmin.getClientPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), adminNo1.getClientLogin(), adminNo1.getClientPassword(), adminNo1.isClientStatusActive());
        assertNotNull(admin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(admin));
    }

    @Test
    public void clientRepositoryUpdateAdminWithNullLoginTestNegative() {
        adminNo1.setClientLogin(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithEmptyLoginTestNegative() {
        String newLogin = "";
        adminNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        adminNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        adminNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = adminNo1.getClientLogin();
        adminNo1.setClientLogin(newLogin);
        clientRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        String loginAfter = foundAdmin.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateAdminWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = adminNo1.getClientLogin();
        adminNo1.setClientLogin(newLogin);
        clientRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        String loginAfter = foundAdmin.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        adminNo1.setClientLogin(clientLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithNullPasswordTestNegative() {
        adminNo1.setClientPassword(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithEmptyPasswordTestNegative() {
        String newPassword = "";
        adminNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithPasswordTooShortTestNegative() {
        String newPassword = "dddf";
        adminNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithPasswordTooLongTestNegative() {
        String newPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        adminNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    public void clientRepositoryUpdateAdminWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddf";
        String passwordBefore = adminNo1.getClientPassword();
        adminNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        String passwordAfter = foundAdmin.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateAdminWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        String passwordBefore = adminNo1.getClientPassword();
        adminNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = clientRepositoryForTests.findAdminByUUID(adminNo1.getClientID());
        String passwordAfter = foundAdmin.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateAdminWithPasswordThatViolatesRegExTestNegative() {
        String clientPassword = "Some Password";
        adminNo1.setClientPassword(clientPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateClient(adminNo1));
    }

    // Staff update tests

    @Test
    public void clientRepositoryUpdateStaffTestPositive() throws ClientRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = staffNo1.getClientLogin();
        String passwordBefore = staffNo1.getClientPassword();
        staffNo1.setClientLogin(newLogin);
        staffNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        String loginAfter = foundStaff.getClientLogin();
        String passwordAfter = foundStaff.getClientPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), adminNo1.getClientLogin(), adminNo1.getClientPassword(), adminNo1.isClientStatusActive());
        assertNotNull(staff);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staff));
    }

    @Test
    public void clientRepositoryUpdateStaffWithNullLoginTestNegative() {
        staffNo1.setClientLogin(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithEmptyLoginTestNegative() {
        String newLogin = "";
        staffNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        staffNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        staffNo1.setClientLogin(newLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithLoginLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = staffNo1.getClientLogin();
        staffNo1.setClientLogin(newLogin);
        clientRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        String loginAfter = foundStaff.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateStaffWithLoginLengthEqualTo20TestNegative() throws ClientRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = staffNo1.getClientLogin();
        staffNo1.setClientLogin(newLogin);
        clientRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        String loginAfter = foundStaff.getClientLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientRepositoryUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        staffNo1.setClientLogin(clientLogin);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithNullPasswordTestNegative() {
        staffNo1.setClientPassword(null);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithEmptyPasswordTestNegative() {
        String newPassword = "";
        staffNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithPasswordTooShortTestNegative() {
        String newPassword = "dddf";
        staffNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithPasswordTooLongTestNegative() {
        String newPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        staffNo1.setClientPassword(newPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    public void clientRepositoryUpdateStaffWithPasswordLengthEqualTo8TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddf";
        String passwordBefore = staffNo1.getClientPassword();
        staffNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        String passwordAfter = foundStaff.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateStaffWithPasswordLengthEqualTo40TestNegative() throws ClientRepositoryException {
        String newPassword = "dddfdddfdddfdddfdddfdddfdddfdddfdddfdddf";
        String passwordBefore = staffNo1.getClientPassword();
        staffNo1.setClientPassword(newPassword);
        clientRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = clientRepositoryForTests.findStaffByUUID(staffNo1.getClientID());
        String passwordAfter = foundStaff.getClientPassword();
        assertNotNull(passwordAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    public void clientRepositoryUpdateStaffWithPasswordThatViolatesRegExTestNegative() {
        String clientPassword = "Some Password";
        staffNo1.setClientPassword(clientPassword);
        assertThrows(ClientRepositoryUpdateException.class, () -> clientRepositoryForTests.updateStaff(staffNo1));
    }

    // Client delete tests

    @Test
    public void clientRepositoryDeleteClientTestPositive() throws ClientRepositoryException {
        int numOfClientsBefore = clientRepositoryForTests.findAllClients().size();
        UUID removedClientID = clientNo1.getClientID();
        clientRepositoryForTests.delete(clientNo1.getClientID(), "client");
        int numOfClientsAfter = clientRepositoryForTests.findAllClients().size();
        assertNotEquals(numOfClientsBefore, numOfClientsAfter);
        assertEquals(2, numOfClientsBefore);
        assertEquals(1, numOfClientsAfter);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findByUUID(removedClientID));
    }

    @Test
    public void clientRepositoryDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientRepositoryDeleteException.class, () -> clientRepositoryForTests.delete(client.getClientID(), "client"));
    }

    // Admin delete tests

    @Test
    public void clientRepositoryDeleteAdminTestPositive() throws ClientRepositoryException {
        int numOfAdminsBefore = clientRepositoryForTests.findAllAdmins().size();
        UUID removedAdminID = adminNo1.getClientID();
        clientRepositoryForTests.delete(adminNo1.getClientID(), "admin");
        int numOfAdminsAfter = clientRepositoryForTests.findAllAdmins().size();
        assertNotEquals(numOfAdminsBefore, numOfAdminsAfter);
        assertEquals(2, numOfAdminsBefore);
        assertEquals(1, numOfAdminsAfter);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findAdminByUUID(removedAdminID));
    }

    @Test
    public void clientRepositoryDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientRepositoryDeleteException.class, () -> clientRepositoryForTests.delete(admin.getClientID(), "admin"));
    }

    // Staff delete tests

    @Test
    public void clientRepositoryDeleteStaffTestPositive() throws ClientRepositoryException {
        int numOfStaffsBefore = clientRepositoryForTests.findAllStaffs().size();
        UUID removedStaffID = staffNo1.getClientID();
        clientRepositoryForTests.delete(staffNo1.getClientID(), "staff");
        int numOfStaffsAfter = clientRepositoryForTests.findAllStaffs().size();
        assertNotEquals(numOfStaffsBefore, numOfStaffsAfter);
        assertEquals(2, numOfStaffsBefore);
        assertEquals(1, numOfStaffsAfter);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.findStaffByUUID(removedStaffID));
    }

    @Test
    public void clientRepositoryDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(ClientRepositoryDeleteException.class, () -> clientRepositoryForTests.delete(staff.getClientID(), "staff"));
    }
}
