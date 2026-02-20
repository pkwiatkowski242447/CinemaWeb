package pl.pas.gr3.cinema.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private static final String DATABASE_NAME = "test";
    private static AccountRepositoryImpl accountRepositoryForTests;

    private Client clientNo1;
    private Client clientNo2;
    private Admin adminNo1;
    private Admin adminNo2;
    private Staff staffNo1;
    private Staff staffNo2;

    @BeforeAll
    static void init() {
        accountRepositoryForTests = new AccountRepositoryImpl(DATABASE_NAME);
    }

    @BeforeEach
    void addExampleClients() {
        try {
            clientNo1 = accountRepositoryForTests.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = accountRepositoryForTests.createClient("ClientLoginNo2", "ClientPasswordNo2");
            adminNo1 = accountRepositoryForTests.createAdmin("AdminLoginNo1", "AdminPasswordNo1");
            adminNo2 = accountRepositoryForTests.createAdmin("AdminLoginNo2", "AdminPasswordNo2");
            staffNo1 = accountRepositoryForTests.createStaff("StaffLoginNo1", "StaffPasswordNo1");
            staffNo2 = accountRepositoryForTests.createStaff("StaffLoginNo2", "StaffPasswordNo2");
        } catch (Exception exception) {
            throw new RuntimeException("Could not initialize test database while adding clients to it.", exception);
        }
    }

    @AfterEach
    void removeExampleClients() {
        try {
            List<Client> clients = accountRepositoryForTests.findAllClients();
            clients.forEach(client -> accountRepositoryForTests.delete(client.getId(), "client"));

            List<Admin> admins = accountRepositoryForTests.findAllAdmins();
            admins.forEach(admin -> accountRepositoryForTests.delete(admin.getId(), "admin"));

            List<Staff> staffs = accountRepositoryForTests.findAllStaffs();
            staffs.forEach(staff -> accountRepositoryForTests.delete(staff.getId(), "staff"));
        } catch (Exception exception) {
            throw new RuntimeException("Could not remove all clients from the test database after client repository tests.", exception);
        }
    }

    @AfterAll
    static void destroy() {
        accountRepositoryForTests.close();
    }

    // Some mongo tests

    @Test
    void mongoRepositoryFindClientWithClientIDTestPositive() throws UserRepositoryException {
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertEquals(clientNo1.getClass(), foundClient.getClass());
        assertEquals(foundClient.getClass(), Client.class);
    }

    @Test
    void mongoRepositoryFindClientThatIsNotInTheDatabaseWithClientIDTestNegative() {
        Client client = new Client(UUID.randomUUID(), clientNo1.getLogin(), clientNo1.getPassword(), clientNo1.isActive());
        assertNotNull(client);
        assertThrows(UserRepositoryReadException.class, () -> accountRepositoryForTests.findClientByUUID(client.getId()));
    }

    @Test
    void mongoRepositoryFindAdminWithAdminIDTestPositive() throws UserRepositoryException {
        Admin foudnAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foudnAdmin);
        assertEquals(adminNo1, foudnAdmin);
        assertEquals(adminNo1.getClass(), foudnAdmin.getClass());
        assertEquals(foudnAdmin.getClass(), Admin.class);
    }

    @Test
    void mongoRepositoryFindAdminWithAdminThatIsNotInTheDatabaseIDTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), clientNo1.getLogin(), clientNo1.getPassword(), clientNo1.isActive());
        assertNotNull(admin);
        assertThrows(UserRepositoryReadException.class, () -> accountRepositoryForTests.findAdminByUUID(admin.getId()));
    }

    @Test
    void mongoRepositoryFindStaffWithStaffIDTestPositive() throws UserRepositoryException {
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        assertEquals(staffNo1.getClass(), foundStaff.getClass());
        assertEquals(foundStaff.getClass(), Staff.class);
    }

    @Test
    void mongoRepositoryFindStaffThatIsNotInTheDatabaseWithStaffIDTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), clientNo1.getLogin(), clientNo1.getPassword(), clientNo1.isActive());
        assertNotNull(staff);
        assertThrows(UserRepositoryReadException.class, () -> accountRepositoryForTests.findByUUID(staff.getId()));
    }

    // Client create tests

    @Test
    void userRepositoryCreateClientTestPositive() throws UserRepositoryException {
        Client client = accountRepositoryForTests.createClient("SomeLogin", "SomePassword");
        assertNotNull(client);
        Client foundClient = accountRepositoryForTests.findClientByUUID(client.getId());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    void userRepositoryCreateClientWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> accountRepositoryForTests.createClient(clientNo1.getLogin(), "SomePassword"));
    }

    @Test
    void userRepositoryCreateClientWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createClient(null, "SomePassword"));
    }

    @Test
    void userRepositoryCreateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateClientWithLoginTooShortTestNegative() {
        String clientLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateClientWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String clientLogin = "dddfdddf";
        Client client = accountRepositoryForTests.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        Client foundClient = accountRepositoryForTests.findClientByUUID(client.getId());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    void userRepositoryCreateClientWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String clientLogin = "dddfdddfdddfdddfdddf";
        Client client = accountRepositoryForTests.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        Client foundClient = accountRepositoryForTests.findClientByUUID(client.getId());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    void userRepositoryCreateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword"));
    }

    // Admin create tests

    @Test
    void userRepositoryCreateAdminTestPositive() throws UserRepositoryException {
        Admin admin = accountRepositoryForTests.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(admin.getId());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    void userRepositoryCreateAdminWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> accountRepositoryForTests.createAdmin(adminNo1.getLogin(), "SomePassword"));
    }

    @Test
    void userRepositoryCreateAdminWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createAdmin(null, "SomePassword"));
    }

    @Test
    void userRepositoryCreateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateAdminWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String adminLogin = "dddfdddf";
        Admin admin = accountRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(admin.getId());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    void userRepositoryCreateAdminWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String adminLogin = "dddfdddfdddfdddfdddf";
        Admin admin = accountRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(admin.getId());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    void userRepositoryCreateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword"));
    }

    // Staff create tests

    @Test
    void userRepositoryCreateStaffTestPositive() throws UserRepositoryException {
        Staff staff = accountRepositoryForTests.createStaff("SomeLogin", "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staff.getId());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    void userRepositoryCreateStaffWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(UserRepositoryCreateUserDuplicateLoginException.class, () -> accountRepositoryForTests.createStaff(staffNo1.getLogin(), "SomePassword"));
    }

    @Test
    void userRepositoryCreateStaffWithNullLoginTestNegative() {
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createStaff(null, "SomePassword"));
    }

    @Test
    void userRepositoryCreateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "dddf";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    @Test
    void userRepositoryCreateStaffWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String staffLogin = "dddfdddf";
        Staff staff = accountRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staff.getId());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    void userRepositoryCreateStaffWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String staffLogin = "dddfdddfdddfdddfdddf";
        Staff staff = accountRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staff.getId());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    void userRepositoryCreateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Login";
        assertThrows(UserRepositoryCreateException.class, () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword"));
    }

    // Find client by ID tests

    @Test
    void userRepositoryFindClientByIDTestPositive() throws UserRepositoryException {
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    void userRepositoryFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(UserRepositoryUserNotFoundException.class, () -> accountRepositoryForTests.findClientByUUID(client.getId()));
    }

    @Test
    void userRepositoryFindAdminByIDTestPositive() throws UserRepositoryException {
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    void userRepositoryFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(UserRepositoryAdminNotFoundException.class, () -> accountRepositoryForTests.findAdminByUUID(admin.getId()));
    }

    @Test
    void userRepositoryFindStaffByIDTestPositive() throws UserRepositoryException {
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    void userRepositoryFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(UserRepositoryStaffNotFoundException.class, () -> accountRepositoryForTests.findStaffByUUID(staff.getId()));
    }

    // Find all users

    @Test
    void userRepositoryFindAllClientsTestPositive() throws UserRepositoryException {
        List<Client> listOfAllClients = accountRepositoryForTests.findAllClients();
        assertNotNull(listOfAllClients);
        assertFalse(listOfAllClients.isEmpty());
        assertEquals(2, listOfAllClients.size());
    }

    @Test
    void userRepositoryFindAllAdminsTestPositive() throws UserRepositoryException {
        List<Admin> listOfAllAdmins = accountRepositoryForTests.findAllAdmins();
        assertNotNull(listOfAllAdmins);
        assertFalse(listOfAllAdmins.isEmpty());
        assertEquals(2, listOfAllAdmins.size());
    }

    @Test
    void userRepositoryFindAllStaffsTestPositive() throws UserRepositoryException {
        List<Staff> listOfAllStaffs = accountRepositoryForTests.findAllStaffs();
        assertNotNull(listOfAllStaffs);
        assertFalse(listOfAllStaffs.isEmpty());
        assertEquals(2, listOfAllStaffs.size());
    }

    // Find users by logins

    @Test
    void userRepositoryFindClientByLoginTestPositive() throws UserRepositoryException {
        String clientLogin = clientNo1.getLogin();
        Client foundClient = accountRepositoryForTests.findClientByLogin(clientLogin);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    void userRepositoryFindAdminByLoginTestPositive() throws UserRepositoryException {
        String adminLogin = adminNo1.getLogin();
        Admin foundAdmin = accountRepositoryForTests.findAdminByLogin(adminLogin);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    void userRepositoryFindStaffByLoginTestPositive() throws UserRepositoryException {
        String staffLogin = staffNo1.getLogin();
        Staff foundStaff = accountRepositoryForTests.findStaffByLogin(staffLogin);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    // Find users by login that is not in the database

    @Test
    void userRepositoryFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(UserRepositoryUserNotFoundException.class, () -> accountRepositoryForTests.findClientByLogin(client.getLogin()));
    }

    @Test
    void userRepositoryFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(UserRepositoryAdminNotFoundException.class, () -> accountRepositoryForTests.findAdminByLogin(admin.getLogin()));
    }

    @Test
    void userRepositoryFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(UserRepositoryStaffNotFoundException.class, () -> accountRepositoryForTests.findStaffByLogin(staff.getLogin()));
    }

    // Find all users matching login

    @Test
    void userRepositoryFindAllClientsMatchingLoginTestPositive() throws UserRepositoryException {
        String clientLogin = "Client";
        List<Client> listOfFoundClients = accountRepositoryForTests.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertFalse(listOfFoundClients.isEmpty());
        assertEquals(2, listOfFoundClients.size());
    }

    @Test
    void userRepositoryFindAllClientsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws UserRepositoryException {
        String clientLogin = "NonExistentLogin";
        List<Client> listOfFoundClients = accountRepositoryForTests.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertTrue(listOfFoundClients.isEmpty());
    }

    @Test
    void userRepositoryFindAllAdminsMatchingLoginTestPositive() throws UserRepositoryException {
        String adminLogin = "Admin";
        List<Admin> listOfFoundAdmins = accountRepositoryForTests.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertFalse(listOfFoundAdmins.isEmpty());
        assertEquals(2, listOfFoundAdmins.size());
    }

    @Test
    void userRepositoryFindAllAdminsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws UserRepositoryException {
        String adminLogin = "NonExistentLogin";
        List<Admin> listOfFoundAdmins = accountRepositoryForTests.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertTrue(listOfFoundAdmins.isEmpty());
    }

    @Test
    void userRepositoryFindAllStaffsMatchingLoginTestPositive() throws UserRepositoryException {
        String staffLogin = "Staff";
        List<Staff> listOfFoundStaffs = accountRepositoryForTests.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertFalse(listOfFoundStaffs.isEmpty());
        assertEquals(2, listOfFoundStaffs.size());
    }

    @Test
    void userRepositoryFindAllStaffsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() throws UserRepositoryException {
        String staffLogin = "NonExistentLogin";
        List<Staff> listOfFoundStaffs = accountRepositoryForTests.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertTrue(listOfFoundStaffs.isEmpty());
    }

    // Activate users tests positive

    @Test
    void userRepositoryActivateClientTestPositive() throws UserRepositoryException {
        clientNo1.setActive(false);
        accountRepositoryForTests.updateClient(clientNo1);
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertFalse(foundClient.isActive());
        accountRepositoryForTests.activate(clientNo1, UserConstants.CLIENT_DISCRIMINATOR);
        foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertTrue(foundClient.isActive());
    }

    @Test
    void userRepositoryActivateAdminTestPositive() throws UserRepositoryException {
        adminNo1.setActive(false);
        accountRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isActive());
        accountRepositoryForTests.activate(adminNo1, UserConstants.ADMIN_DISCRIMINATOR);
        foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isActive());
    }

    @Test
    void userRepositoryActivateStaffTestPositive() throws UserRepositoryException {
        staffNo1.setActive(false);
        accountRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isActive());
        accountRepositoryForTests.activate(staffNo1, UserConstants.STAFF_DISCRIMINATOR);
        foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isActive());
    }

    // Deactivate users test negative

    @Test
    void userRepositoryActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserActivationException.class, () -> accountRepositoryForTests.activate(client, UserConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    void userRepositoryActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserActivationException.class, () -> accountRepositoryForTests.activate(admin, UserConstants.ADMIN_DISCRIMINATOR));
    }

    @Test
    void userRepositoryActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserActivationException.class, () -> accountRepositoryForTests.activate(staff, UserConstants.STAFF_DISCRIMINATOR));
    }

    // Deactivate users tests positive

    @Test
    void userRepositoryDeactivateClientTestPositive() throws UserRepositoryException {
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertTrue(foundClient.isActive());
        accountRepositoryForTests.deactivate(clientNo1, UserConstants.CLIENT_DISCRIMINATOR);
        foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertFalse(foundClient.isActive());
    }

    @Test
    void userRepositoryDeactivateAdminTestPositive() throws UserRepositoryException {
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isActive());
        accountRepositoryForTests.deactivate(adminNo1, UserConstants.ADMIN_DISCRIMINATOR);
        foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isActive());
    }

    @Test
    void userRepositoryDeactivateStaffTestPositive() throws UserRepositoryException {
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isActive());
        accountRepositoryForTests.deactivate(staffNo1, UserConstants.STAFF_DISCRIMINATOR);
        foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isActive());
    }

    // Deactivate users test negative

    @Test
    void userRepositoryDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserDeactivationException.class, () -> accountRepositoryForTests.deactivate(client, UserConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    void userRepositoryDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserDeactivationException.class, () -> accountRepositoryForTests.deactivate(admin, UserConstants.ADMIN_DISCRIMINATOR));
    }

    @Test
    void userRepositoryDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserDeactivationException.class, () -> accountRepositoryForTests.deactivate(staff, UserConstants.STAFF_DISCRIMINATOR));
    }

    // Update client tests

    @Test
    void userRepositoryUpdateClientTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = clientNo1.getLogin();
        String passwordBefore = clientNo1.getPassword();
        clientNo1.setLogin(newLogin);
        clientNo1.setPassword(newPassword);
        accountRepositoryForTests.updateClient(clientNo1);
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        String loginAfter = foundClient.getLogin();
        String passwordAfter = foundClient.getPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    void userRepositoryUpdateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), clientNo1.getLogin(), clientNo1.getPassword(), clientNo1.isActive());
        assertNotNull(client);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateClient(client));
    }

    @Test
    void userRepositoryUpdateClientWithNullLoginTestNegative() {
        clientNo1.setLogin(null);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithEmptyLoginTestNegative() {
        String newLogin = "";
        clientNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        clientNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        clientNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = clientNo1.getLogin();
        clientNo1.setLogin(newLogin);
        accountRepositoryForTests.updateClient(clientNo1);
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        String loginAfter = foundClient.getLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void userRepositoryUpdateClientWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = clientNo1.getLogin();
        clientNo1.setLogin(newLogin);
        accountRepositoryForTests.updateClient(clientNo1);
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        String loginAfter = foundClient.getLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void userRepositoryUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        clientNo1.setLogin(clientLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    // Update admin tests

    @Test
    void userRepositoryUpdateAdminTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = adminNo1.getLogin();
        String passwordBefore = adminNo1.getPassword();
        adminNo1.setLogin(newLogin);
        adminNo1.setPassword(newPassword);
        accountRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        String loginAfter = foundAdmin.getLogin();
        String passwordAfter = foundAdmin.getPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    void userRepositoryUpdateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), adminNo1.getLogin(), adminNo1.getPassword(), adminNo1.isActive());
        assertNotNull(admin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateAdmin(admin));
    }

    @Test
    void userRepositoryUpdateAdminWithNullLoginTestNegative() {
        adminNo1.setLogin(null);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithEmptyLoginTestNegative() {
        String newLogin = "";
        adminNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        adminNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        adminNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = adminNo1.getLogin();
        adminNo1.setLogin(newLogin);
        accountRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        String loginAfter = foundAdmin.getLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void userRepositoryUpdateAdminWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = adminNo1.getLogin();
        adminNo1.setLogin(newLogin);
        accountRepositoryForTests.updateAdmin(adminNo1);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        String loginAfter = foundAdmin.getLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void userRepositoryUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        adminNo1.setLogin(clientLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    // Staff update tests

    @Test
    void userRepositoryUpdateStaffTestPositive() throws UserRepositoryException {
        String newLogin = "NewLogin";
        String newPassword = "NewPassword";
        String loginBefore = staffNo1.getLogin();
        String passwordBefore = staffNo1.getPassword();
        staffNo1.setLogin(newLogin);
        staffNo1.setPassword(newPassword);
        accountRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        String loginAfter = foundStaff.getLogin();
        String passwordAfter = foundStaff.getPassword();
        assertNotNull(loginAfter);
        assertNotNull(passwordAfter);
        assertEquals(newLogin, loginAfter);
        assertEquals(newPassword, passwordAfter);
        assertNotEquals(loginBefore, loginAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    void userRepositoryUpdateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), adminNo1.getLogin(), adminNo1.getPassword(), adminNo1.isActive());
        assertNotNull(staff);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateStaff(staff));
    }

    @Test
    void userRepositoryUpdateStaffWithNullLoginTestNegative() {
        staffNo1.setLogin(null);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithEmptyLoginTestNegative() {
        String newLogin = "";
        staffNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        staffNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        staffNo1.setLogin(newLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithLoginLengthEqualTo8TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddf";
        String loginBefore = staffNo1.getLogin();
        staffNo1.setLogin(newLogin);
        accountRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        String loginAfter = foundStaff.getLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void userRepositoryUpdateStaffWithLoginLengthEqualTo20TestNegative() throws UserRepositoryException {
        String newLogin = "dddfdddfdddfdddfdddf";
        String loginBefore = staffNo1.getLogin();
        staffNo1.setLogin(newLogin);
        accountRepositoryForTests.updateStaff(staffNo1);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        String loginAfter = foundStaff.getLogin();
        assertNotNull(loginAfter);
        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void userRepositoryUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Login";
        staffNo1.setLogin(clientLogin);
        assertThrows(UserRepositoryUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    // Client delete tests

    @Test
    void userRepositoryDeleteClientTestPositive() throws UserRepositoryException {
        int numOfClientsBefore = accountRepositoryForTests.findAllClients().size();
        UUID removedClientID = clientNo1.getId();
        accountRepositoryForTests.delete(clientNo1.getId(), UserConstants.CLIENT_DISCRIMINATOR);
        int numOfClientsAfter = accountRepositoryForTests.findAllClients().size();
        assertNotEquals(numOfClientsBefore, numOfClientsAfter);
        assertEquals(2, numOfClientsBefore);
        assertEquals(1, numOfClientsAfter);
        assertThrows(UserRepositoryReadException.class, () -> accountRepositoryForTests.findByUUID(removedClientID));
    }

    @Test
    void userRepositoryDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(client.getId(), UserConstants.CLIENT_DISCRIMINATOR));
    }

    // Admin delete tests

    @Test
    void userRepositoryDeleteAdminTestPositive() throws UserRepositoryException {
        int numOfAdminsBefore = accountRepositoryForTests.findAllAdmins().size();
        UUID removedAdminID = adminNo1.getId();
        accountRepositoryForTests.delete(adminNo1.getId(), UserConstants.ADMIN_DISCRIMINATOR);
        int numOfAdminsAfter = accountRepositoryForTests.findAllAdmins().size();
        assertNotEquals(numOfAdminsBefore, numOfAdminsAfter);
        assertEquals(2, numOfAdminsBefore);
        assertEquals(1, numOfAdminsAfter);
        assertThrows(UserRepositoryReadException.class, () -> accountRepositoryForTests.findAdminByUUID(removedAdminID));
    }

    @Test
    void userRepositoryDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(admin.getId(), UserConstants.ADMIN_DISCRIMINATOR));
    }

    // Staff delete tests

    @Test
    void userRepositoryDeleteStaffTestPositive() throws UserRepositoryException {
        int numOfStaffsBefore = accountRepositoryForTests.findAllStaffs().size();
        UUID removedStaffID = staffNo1.getId();
        accountRepositoryForTests.delete(staffNo1.getId(), UserConstants.STAFF_DISCRIMINATOR);
        int numOfStaffsAfter = accountRepositoryForTests.findAllStaffs().size();
        assertNotEquals(numOfStaffsBefore, numOfStaffsAfter);
        assertEquals(2, numOfStaffsBefore);
        assertEquals(1, numOfStaffsAfter);
        assertThrows(UserRepositoryReadException.class, () -> accountRepositoryForTests.findStaffByUUID(removedStaffID));
    }

    @Test
    void userRepositoryDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(staff.getId(), UserConstants.STAFF_DISCRIMINATOR));
    }

    // Delete user with wrong discriminator

    @Test
    void userRepositoryTryDeletingClientWithAdminDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(clientNo1.getId(), UserConstants.ADMIN_DISCRIMINATOR));
    }

    @Test
    void userRepositoryTryDeletingClientWithStaffDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(clientNo1.getId(), UserConstants.STAFF_DISCRIMINATOR));
    }

    @Test
    void userRepositoryTryDeletingAdminWithClientDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(adminNo1.getId(), UserConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    void userRepositoryTryDeletingAdminWithStaffDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(adminNo1.getId(), UserConstants.STAFF_DISCRIMINATOR));
    }

    @Test
    void userRepositoryTryDeletingStaffWithClientDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(staffNo1.getId(), UserConstants.CLIENT_DISCRIMINATOR));
    }

    @Test
    void userRepositoryTryDeletingStaffWithAdminDiscriminatorTestNegative() {
        assertThrows(UserRepositoryDeleteException.class, () -> accountRepositoryForTests.delete(staffNo1.getId(), UserConstants.ADMIN_DISCRIMINATOR));
    }
}