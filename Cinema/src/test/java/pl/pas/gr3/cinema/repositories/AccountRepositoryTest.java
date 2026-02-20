package pl.pas.gr3.cinema.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import pl.pas.gr3.cinema.exception.bad_request.AccountActivationException;
import pl.pas.gr3.cinema.exception.bad_request.AccountCreateException;
import pl.pas.gr3.cinema.exception.bad_request.AccountDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.AccountUpdateException;
import pl.pas.gr3.cinema.exception.conflict.LoginAlreadyTakenException;
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AccountRepositoryTest {

    private static final String DATABASE_NAME = "test";
    private static AccountRepositoryImpl accountRepositoryForTests;

    private Client clientNo1;
    private Client clientNo2;
    private Admin adminNo1;
    private Admin adminNo2;
    private Staff staffNo1;
    private Staff staffNo2;

    private AccountMapper accountMapper;

    @BeforeAll
    static void init() {
        accountRepositoryForTests = new AccountRepositoryImpl(DATABASE_NAME);
    }

    @BeforeEach
    void addExampleClients() {
        accountMapper = Mappers.getMapper(AccountMapper.class);
        ReflectionTestUtils.setField(accountRepositoryForTests, "accountMapper", accountMapper);

        cleanDatabaseState();

        try {
            clientNo1 = accountRepositoryForTests.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = accountRepositoryForTests.createClient("ClientLoginNo2", "ClientPasswordNo2");
            adminNo1 = accountRepositoryForTests.createAdmin("AdminLoginNo1", "AdminPasswordNo1");
            adminNo2 = accountRepositoryForTests.createAdmin("AdminLoginNo2", "AdminPasswordNo2");
            staffNo1 = accountRepositoryForTests.createStaff("StaffLoginNo1", "StaffPasswordNo1");
            staffNo2 = accountRepositoryForTests.createStaff("StaffLoginNo2", "StaffPasswordNo2");
        } catch (Exception exception) {
            throw new RuntimeException("Could not initialize test database while adding test data.", exception);
        }
    }

    private void cleanDatabaseState() {
        try {
            List<Client> clients = accountRepositoryForTests.findAllClients();
            clients.forEach(client -> accountRepositoryForTests.delete(client.getId(), "client"));

            List<Admin> admins = accountRepositoryForTests.findAllAdmins();
            admins.forEach(admin -> accountRepositoryForTests.delete(admin.getId(), "admin"));

            List<Staff> staffs = accountRepositoryForTests.findAllStaffs();
            staffs.forEach(staff -> accountRepositoryForTests.delete(staff.getId(), "staff"));
        } catch (Exception exception) {
            log.error("Could not remove all clients from the test database after client repository tests.", exception);
        }
    }

    @AfterAll
    static void destroy() {
        accountRepositoryForTests.close();
    }

    // Some mongo tests

    @Test
    void mongoRepositoryFindClientWithClientIDTestPositive() {
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
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.findClientByUUID(client.getId()));
    }

    @Test
    void mongoRepositoryFindAdminWithAdminIDTestPositive() {
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
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.findAdminByUUID(admin.getId()));
    }

    @Test
    void mongoRepositoryFindStaffWithStaffIDTestPositive() {
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
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.findByUUID(staff.getId()));
    }

    // Client create tests

    @Test
    void userRepositoryCreateClientTestPositive() {
        Client client = accountRepositoryForTests.createClient("SomeLogin", "SomePassword");
        assertNotNull(client);
        Client foundClient = accountRepositoryForTests.findClientByUUID(client.getId());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    void userRepositoryCreateClientWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(LoginAlreadyTakenException.class,
            () -> accountRepositoryForTests.createClient(clientNo1.getLogin(), "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateClientWithNullLoginTestNegative() {
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createClient(null, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateClientWithLoginTooShortTestNegative() {
        String clientLogin = "dddf";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateClientWithLoginLengthEqualTo8TestNegative() {
        String clientLogin = "dddfdddf";
        Client client = accountRepositoryForTests.createClient(clientLogin, "SomePassword");
        assertNotNull(client);
        Client foundClient = accountRepositoryForTests.findClientByUUID(client.getId());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    void userRepositoryCreateClientWithLoginLengthEqualTo20TestNegative() {
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
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createClient(clientLogin, "SomePassword")
        );
    }

    // Admin create tests

    @Test
    void userRepositoryCreateAdminTestPositive() {
        Admin admin = accountRepositoryForTests.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(admin.getId());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    void userRepositoryCreateAdminWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(LoginAlreadyTakenException.class,
            () -> accountRepositoryForTests.createAdmin(adminNo1.getLogin(), "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateAdminWithNullLoginTestNegative() {
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createAdmin(null, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "dddf";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateAdminWithLoginLengthEqualTo8TestNegative() {
        String adminLogin = "dddfdddf";
        Admin admin = accountRepositoryForTests.createAdmin(adminLogin, "SomePassword");
        assertNotNull(admin);
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(admin.getId());
        assertNotNull(foundAdmin);
        assertEquals(admin, foundAdmin);
    }

    @Test
    void userRepositoryCreateAdminWithLoginLengthEqualTo20TestNegative() {
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
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createAdmin(adminLogin, "SomePassword")
        );
    }

    // Staff create tests

    @Test
    void userRepositoryCreateStaffTestPositive() {
        Staff staff = accountRepositoryForTests.createStaff("SomeLogin", "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staff.getId());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    void userRepositoryCreateStaffWithLoginAlreadyInTheDatabaseTestNegative() {
        assertThrows(LoginAlreadyTakenException.class,
            () -> accountRepositoryForTests.createStaff(staffNo1.getLogin(), "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateStaffWithNullLoginTestNegative() {
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createStaff(null, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "dddf";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword")
        );
    }

    @Test
    void userRepositoryCreateStaffWithLoginLengthEqualTo8TestNegative() {
        String staffLogin = "dddfdddf";
        Staff staff = accountRepositoryForTests.createStaff(staffLogin, "SomePassword");
        assertNotNull(staff);
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staff.getId());
        assertNotNull(foundStaff);
        assertEquals(staff, foundStaff);
    }

    @Test
    void userRepositoryCreateStaffWithLoginLengthEqualTo20TestNegative() {
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
        assertThrows(AccountCreateException.class,
            () -> accountRepositoryForTests.createStaff(staffLogin, "SomePassword")
        );
    }

    // Find client by ID tests

    @Test
    void userRepositoryFindClientByIDTestPositive() {
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    void userRepositoryFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(client);
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.findClientByUUID(client.getId())
        );
    }

    @Test
    void userRepositoryFindAdminByIDTestPositive() {
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    void userRepositoryFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.findAdminByUUID(admin.getId())
        );
    }

    @Test
    void userRepositoryFindStaffByIDTestPositive() {
        Staff foundStaff = accountRepositoryForTests.findStaffByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    void userRepositoryFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.findStaffByUUID(staff.getId())
        );
    }

    // Find all users

    @Test
    void userRepositoryFindAllClientsTestPositive() {
        List<Client> listOfAllClients = accountRepositoryForTests.findAllClients();
        assertNotNull(listOfAllClients);
        assertFalse(listOfAllClients.isEmpty());
        assertEquals(2, listOfAllClients.size());
    }

    @Test
    void userRepositoryFindAllAdminsTestPositive() {
        List<Admin> listOfAllAdmins = accountRepositoryForTests.findAllAdmins();
        assertNotNull(listOfAllAdmins);
        assertFalse(listOfAllAdmins.isEmpty());
        assertEquals(2, listOfAllAdmins.size());
    }

    @Test
    void userRepositoryFindAllStaffsTestPositive() {
        List<Staff> listOfAllStaffs = accountRepositoryForTests.findAllStaffs();
        assertNotNull(listOfAllStaffs);
        assertFalse(listOfAllStaffs.isEmpty());
        assertEquals(2, listOfAllStaffs.size());
    }

    // Find users by logins

    @Test
    void userRepositoryFindClientByLoginTestPositive() {
        String clientLogin = clientNo1.getLogin();
        Client foundClient = accountRepositoryForTests.findClientByLogin(clientLogin);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    void userRepositoryFindAdminByLoginTestPositive() {
        String adminLogin = adminNo1.getLogin();
        Admin foundAdmin = accountRepositoryForTests.findAdminByLogin(adminLogin);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    void userRepositoryFindStaffByLoginTestPositive() {
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
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.findClientByLogin(client.getLogin())
        );
    }

    @Test
    void userRepositoryFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.findAdminByLogin(admin.getLogin())
        );
    }

    @Test
    void userRepositoryFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.findStaffByLogin(staff.getLogin())
        );
    }

    // Find all users matching login

    @Test
    void userRepositoryFindAllClientsMatchingLoginTestPositive() {
        String clientLogin = "Client";
        List<Client> listOfFoundClients = accountRepositoryForTests.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertFalse(listOfFoundClients.isEmpty());
        assertEquals(2, listOfFoundClients.size());
    }

    @Test
    void userRepositoryFindAllClientsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() {
        String clientLogin = "NonExistentLogin";
        List<Client> listOfFoundClients = accountRepositoryForTests.findAllClientsMatchingLogin(clientLogin);
        assertNotNull(listOfFoundClients);
        assertTrue(listOfFoundClients.isEmpty());
    }

    @Test
    void userRepositoryFindAllAdminsMatchingLoginTestPositive() {
        String adminLogin = "Admin";
        List<Admin> listOfFoundAdmins = accountRepositoryForTests.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertFalse(listOfFoundAdmins.isEmpty());
        assertEquals(2, listOfFoundAdmins.size());
    }

    @Test
    void userRepositoryFindAllAdminsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() {
        String adminLogin = "NonExistentLogin";
        List<Admin> listOfFoundAdmins = accountRepositoryForTests.findAllAdminsMatchingLogin(adminLogin);
        assertNotNull(listOfFoundAdmins);
        assertTrue(listOfFoundAdmins.isEmpty());
    }

    @Test
    void userRepositoryFindAllStaffsMatchingLoginTestPositive() {
        String staffLogin = "Staff";
        List<Staff> listOfFoundStaffs = accountRepositoryForTests.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertFalse(listOfFoundStaffs.isEmpty());
        assertEquals(2, listOfFoundStaffs.size());
    }

    @Test
    void userRepositoryFindAllStaffsMatchingLoginWhenLoginIsNotInTheDatabaseTestPositive() {
        String staffLogin = "NonExistentLogin";
        List<Staff> listOfFoundStaffs = accountRepositoryForTests.findAllStaffsMatchingLogin(staffLogin);
        assertNotNull(listOfFoundStaffs);
        assertTrue(listOfFoundStaffs.isEmpty());
    }

    // Activate users tests positive

    @Test
    void userRepositoryActivateClientTestPositive() {
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
    void userRepositoryActivateAdminTestPositive() {
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
    void userRepositoryActivateStaffTestPositive() {
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
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.activate(client, UserConstants.CLIENT_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.activate(admin, UserConstants.ADMIN_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.activate(staff, UserConstants.STAFF_DISCRIMINATOR)
        );
    }

    // Deactivate users tests positive

    @Test
    void userRepositoryDeactivateClientTestPositive() {
        Client foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertTrue(foundClient.isActive());
        accountRepositoryForTests.deactivate(clientNo1, UserConstants.CLIENT_DISCRIMINATOR);
        foundClient = accountRepositoryForTests.findClientByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertFalse(foundClient.isActive());
    }

    @Test
    void userRepositoryDeactivateAdminTestPositive() {
        Admin foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isActive());
        accountRepositoryForTests.deactivate(adminNo1, UserConstants.ADMIN_DISCRIMINATOR);
        foundAdmin = accountRepositoryForTests.findAdminByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isActive());
    }

    @Test
    void userRepositoryDeactivateStaffTestPositive() {
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
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.deactivate(client, UserConstants.CLIENT_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.deactivate(admin, UserConstants.ADMIN_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.deactivate(staff, UserConstants.STAFF_DISCRIMINATOR)
        );
    }

    // Update client tests

    @Test
    void userRepositoryUpdateClientTestPositive() {
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
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.updateClient(client));
    }

    @Test
    void userRepositoryUpdateClientWithNullLoginTestNegative() {
        clientNo1.setLogin(null);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithEmptyLoginTestNegative() {
        String newLogin = "";
        clientNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        clientNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        clientNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    @Test
    void userRepositoryUpdateClientWithLoginLengthEqualTo8TestNegative() {
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
    void userRepositoryUpdateClientWithLoginLengthEqualTo20TestNegative() {
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
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateClient(clientNo1));
    }

    // Update admin tests

    @Test
    void userRepositoryUpdateAdminTestPositive() {
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
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.updateAdmin(admin));
    }

    @Test
    void userRepositoryUpdateAdminWithNullLoginTestNegative() {
        adminNo1.setLogin(null);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithEmptyLoginTestNegative() {
        String newLogin = "";
        adminNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        adminNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        adminNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    @Test
    void userRepositoryUpdateAdminWithLoginLengthEqualTo8TestNegative() {
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
    void userRepositoryUpdateAdminWithLoginLengthEqualTo20TestNegative() {
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
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateAdmin(adminNo1));
    }

    // Staff update tests

    @Test
    void userRepositoryUpdateStaffTestPositive() {
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
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.updateStaff(staff));
    }

    @Test
    void userRepositoryUpdateStaffWithNullLoginTestNegative() {
        staffNo1.setLogin(null);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithEmptyLoginTestNegative() {
        String newLogin = "";
        staffNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithLoginTooShortTestNegative() {
        String newLogin = "dddf";
        staffNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithLoginTooLongTestNegative() {
        String newLogin = "ddddfddddfddddfddddfd";
        staffNo1.setLogin(newLogin);
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    @Test
    void userRepositoryUpdateStaffWithLoginLengthEqualTo8TestNegative() {
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
    void userRepositoryUpdateStaffWithLoginLengthEqualTo20TestNegative() {
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
        assertThrows(AccountUpdateException.class, () -> accountRepositoryForTests.updateStaff(staffNo1));
    }

    // Client delete tests

    @Test
    void userRepositoryDeleteClientTestPositive() {
        int numOfClientsBefore = accountRepositoryForTests.findAllClients().size();
        UUID removedClientID = clientNo1.getId();
        accountRepositoryForTests.delete(clientNo1.getId(), UserConstants.CLIENT_DISCRIMINATOR);
        int numOfClientsAfter = accountRepositoryForTests.findAllClients().size();
        assertNotEquals(numOfClientsBefore, numOfClientsAfter);
        assertEquals(2, numOfClientsBefore);
        assertEquals(1, numOfClientsAfter);
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.findByUUID(removedClientID));
    }

    @Test
    void userRepositoryDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.delete(client.getId(), UserConstants.CLIENT_DISCRIMINATOR));
    }

    // Admin delete tests

    @Test
    void userRepositoryDeleteAdminTestPositive() {
        int numOfAdminsBefore = accountRepositoryForTests.findAllAdmins().size();
        UUID removedAdminID = adminNo1.getId();
        accountRepositoryForTests.delete(adminNo1.getId(), UserConstants.ADMIN_DISCRIMINATOR);
        int numOfAdminsAfter = accountRepositoryForTests.findAllAdmins().size();
        assertNotEquals(numOfAdminsBefore, numOfAdminsAfter);
        assertEquals(2, numOfAdminsBefore);
        assertEquals(1, numOfAdminsAfter);
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.findAdminByUUID(removedAdminID));
    }

    @Test
    void userRepositoryDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.delete(admin.getId(), UserConstants.ADMIN_DISCRIMINATOR));
    }

    // Staff delete tests

    @Test
    void userRepositoryDeleteStaffTestPositive() {
        int numOfStaffsBefore = accountRepositoryForTests.findAllStaffs().size();
        UUID removedStaffID = staffNo1.getId();
        accountRepositoryForTests.delete(staffNo1.getId(), UserConstants.STAFF_DISCRIMINATOR);
        int numOfStaffsAfter = accountRepositoryForTests.findAllStaffs().size();
        assertNotEquals(numOfStaffsBefore, numOfStaffsAfter);
        assertEquals(2, numOfStaffsBefore);
        assertEquals(1, numOfStaffsAfter);
        assertThrows(AccountNotFoundException.class, () -> accountRepositoryForTests.findStaffByUUID(removedStaffID));
    }

    @Test
    void userRepositoryDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeLogin", "SomePassword");
        assertThrows(AccountNotFoundException.class,
            () -> accountRepositoryForTests.delete(staff.getId(), UserConstants.STAFF_DISCRIMINATOR)
        );
    }

    // Delete user with wrong discriminator

    @Test
    void userRepositoryTryDeletingClientWithAdminDiscriminatorTestNegative() {
        assertThrows(AccountDeleteException.class,
            () -> accountRepositoryForTests.delete(clientNo1.getId(), UserConstants.ADMIN_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryTryDeletingClientWithStaffDiscriminatorTestNegative() {
        assertThrows(AccountDeleteException.class,
            () -> accountRepositoryForTests.delete(clientNo1.getId(), UserConstants.STAFF_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryTryDeletingAdminWithClientDiscriminatorTestNegative() {
        assertThrows(AccountDeleteException.class,
            () -> accountRepositoryForTests.delete(adminNo1.getId(), UserConstants.CLIENT_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryTryDeletingAdminWithStaffDiscriminatorTestNegative() {
        assertThrows(AccountDeleteException.class,
            () -> accountRepositoryForTests.delete(adminNo1.getId(), UserConstants.STAFF_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryTryDeletingStaffWithClientDiscriminatorTestNegative() {
        assertThrows(AccountDeleteException.class,
            () -> accountRepositoryForTests.delete(staffNo1.getId(), UserConstants.CLIENT_DISCRIMINATOR)
        );
    }

    @Test
    void userRepositoryTryDeletingStaffWithAdminDiscriminatorTestNegative() {
        assertThrows(AccountDeleteException.class,
            () -> accountRepositoryForTests.delete(staffNo1.getId(), UserConstants.ADMIN_DISCRIMINATOR)
        );
    }
}