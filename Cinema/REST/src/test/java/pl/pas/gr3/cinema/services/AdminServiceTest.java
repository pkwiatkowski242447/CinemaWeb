package pl.pas.gr3.cinema.services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.services.GeneralAdminServiceException;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.*;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;
import pl.pas.gr3.cinema.services.implementations.AdminService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(AdminServiceTest.class);

    private final static String databaseName = "test";
    private static ClientRepository clientRepository;
    private static AdminService adminService;

    private Admin adminNo1;
    private Admin adminNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        adminService = new AdminService(clientRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            adminNo1 = adminService.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminService.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        } catch (AdminServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Admin> listOfAdmins = adminService.findAll();
            for (Admin admin : listOfAdmins) {
                adminService.delete(admin.getClientID());
            }
        } catch (AdminServiceReadException | AdminServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        clientRepository.close();
    }

    // Create tests

    @Test
    public void adminManagerCreateAdminTestPositive() throws AdminServiceCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithNullLoginThatTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithEmptyLoginThatTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginTooShortThatTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginTooLongThatTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginLengthEqualTo8ThatTestPositive() throws AdminServiceCreateException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithLoginLengthEqualTo20ThatTestNegative() throws AdminServiceCreateException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginThatIsAlreadyInTheDatabaseTestNegative() {
        String adminLogin = adminNo1.getClientLogin();
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateAdminDuplicateLoginException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithNullPasswordThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = null;
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithEmptyPasswordThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithPasswordTooShortThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfdd";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithPasswordTooLongThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithPasswordLengthEqualTo8ThatTestNegative() throws AdminServiceCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfddd";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithPasswordLengthEqualTo40ThatTestNegative() throws AdminServiceCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithPasswordThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "Some Invalid Password";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    // Read tests

    @Test
    public void adminManagerFindAdminByIDTestPositive() throws AdminServiceReadException {
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void adminManagerFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByUUID(admin.getClientID()));
    }

    @Test
    public void adminManagerFindAdminByLoginTestPositive() throws AdminServiceReadException {
        Admin foundAdmin = adminService.findByLogin(adminNo1.getClientLogin());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void adminManagerFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByLogin(admin.getClientLogin()));
    }

    @Test
    public void adminManagerFindAllAdminsMatchingLoginTestPositive() throws AdminServiceCreateException, AdminServiceReadException {
        adminService.create("NewAdminLogin", "NewAdminPassword");
        List<Admin> listOfAdmins = adminService.findAllMatchingLogin("New");
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        assertEquals(1, listOfAdmins.size());
    }

    @Test
    public void adminManagerFindAllAdminTestPositive() throws AdminServiceReadException {
        List<Admin> listOfAdmins = adminService.findAll();
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        // assertEquals(2, listOfAdmins.size());
    }

    // Update tests

    @Test
    public void adminManagerUpdateAdminTestPositive() throws AdminServiceUpdateException, AdminServiceReadException {
        String adminLoginBefore = adminNo1.getClientLogin();
        String adminPasswordBefore = adminNo1.getClientPassword();
        String newAdminLogin = "OtherNewLoginNo1";
        String newAdminPassword = "OtherNewPasswordNo1";
        adminNo1.setClientLogin(newAdminLogin);
        adminNo1.setClientPassword(newAdminPassword);
        adminService.update(adminNo1);
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        String adminLoginAfter =  foundAdmin.getClientLogin();
        String adminPasswordAfter = foundAdmin.getClientPassword();
        assertNotNull(adminLoginAfter);
        assertNotNull(adminPasswordAfter);
        assertEquals(newAdminLogin, adminLoginAfter);
        assertEquals(newAdminPassword, adminPasswordAfter);
        assertNotEquals(adminLoginBefore, adminLoginAfter);
        assertNotEquals(adminPasswordBefore, adminPasswordAfter);
    }

    @Test
    public void adminManagerUpdateAdminWithNullLoginTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithLoginLengthEqualTo8TestNegative() throws AdminServiceReadException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithLoginLengthEqualTo20TestNegative() throws AdminServiceReadException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithNullPasswordTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = null;
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithEmptyPasswordTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordTooShortTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfdd";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordTooLongTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordLengthEqualTo8TestNegative() throws AdminServiceReadException {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfddd";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordLengthEqualTo40TestNegative() throws AdminServiceReadException {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordThatViolatesRegExTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "Some Invalid Password";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    // Delete tests

    @Test
    public void adminManagerDeleteAdminTestPositive() throws AdminServiceReadException, AdminServiceDeleteException {
        UUID removedAdminUUID = adminNo1.getClientID();
        Admin foundAdmin = adminService.findByUUID(removedAdminUUID);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        adminService.delete(removedAdminUUID);
        assertThrows(AdminServiceReadException.class, () -> adminService.findByUUID(removedAdminUUID));
    }

    @Test
    public void adminManagerDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceDeleteException.class, () -> adminService.delete(admin.getClientID()));
    }

    // Activate tests

    @Test
    public void adminManagerActivateAdminTestPositive() throws GeneralAdminServiceException {
        adminService.deactivate(adminNo1.getClientID());

        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isClientStatusActive());
        adminService.activate(adminNo1.getClientID());
        foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isClientStatusActive());
    }

    @Test
    public void adminManagerDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceActivationException.class, () -> adminService.activate(admin.getClientID()));
    }

    // Deactivate tests

    @Test
    public void adminManagerDeactivateAdminTestPositive() throws GeneralAdminServiceException {
        Admin foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        assertTrue(foundAdmin.isClientStatusActive());
        adminService.deactivate(adminNo1.getClientID());
        foundAdmin = adminService.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isClientStatusActive());
    }

    @Test
    public void adminManagerActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceDeactivationException.class, () -> adminService.deactivate(admin.getClientID()));
    }
}
