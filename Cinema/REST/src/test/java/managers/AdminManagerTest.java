package managers;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.managers.GeneralAdminManagerException;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.*;
import pl.pas.gr3.cinema.managers.implementations.AdminManager;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminManagerTest {

    private final static Logger logger = LoggerFactory.getLogger(AdminManagerTest.class);

    private final static String databaseName = "test";
    private static ClientRepository clientRepository;
    private static AdminManager adminManager;

    private Admin adminNo1;
    private Admin adminNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        adminManager = new AdminManager(clientRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            adminNo1 = adminManager.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminManager.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        } catch (AdminManagerCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Admin> listOfAdmins = adminManager.findAll();
            for (Admin admin : listOfAdmins) {
                adminManager.delete(admin.getClientID());
            }
        } catch (AdminManagerReadException | AdminManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        adminManager.close();
    }

    // Create tests

    @Test
    public void adminManagerCreateAdminTestPositive() throws AdminManagerCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminManager.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithNullLoginThatTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithEmptyLoginThatTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginTooShortThatTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginTooLongThatTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginLengthEqualTo8ThatTestPositive() throws AdminManagerCreateException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminManager.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithLoginLengthEqualTo20ThatTestNegative() throws AdminManagerCreateException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminManager.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithLoginThatIsAlreadyInTheDatabaseTestNegative() {
        String adminLogin = adminNo1.getClientLogin();
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminManagerCreateAdminDuplicateLoginException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithNullPasswordThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = null;
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithEmptyPasswordThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithPasswordTooShortThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfdd";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithPasswordTooLongThatTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    @Test
    public void adminManagerCreateAdminWithPasswordLengthEqualTo8ThatTestNegative() throws AdminManagerCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfddd";
        Admin admin = adminManager.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithPasswordLengthEqualTo40ThatTestNegative() throws AdminManagerCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        Admin admin = adminManager.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getClientLogin());
        assertEquals(adminPassword, admin.getClientPassword());
    }

    @Test
    public void adminManagerCreateAdminWithPasswordThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "Some Invalid Password";
        assertThrows(AdminManagerCreateException.class, () -> adminManager.create(adminLogin, adminPassword));
    }

    // Read tests

    @Test
    public void adminManagerFindAdminByIDTestPositive() throws AdminManagerReadException {
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void adminManagerFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminManagerReadException.class, () -> adminManager.findByUUID(admin.getClientID()));
    }

    @Test
    public void adminManagerFindAdminByLoginTestPositive() throws AdminManagerReadException {
        Admin foundAdmin = adminManager.findByLogin(adminNo1.getClientLogin());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    public void adminManagerFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminManagerReadException.class, () -> adminManager.findByLogin(admin.getClientLogin()));
    }

    @Test
    public void adminManagerFindAllAdminsMatchingLoginTestPositive() throws AdminManagerCreateException, AdminManagerReadException {
        adminManager.create("NewAdminLogin", "NewAdminPassword");
        List<Admin> listOfAdmins = adminManager.findAllMatchingLogin("New");
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        assertEquals(1, listOfAdmins.size());
    }

    @Test
    public void adminManagerFindAllAdminTestPositive() throws AdminManagerReadException {
        List<Admin> listOfAdmins = adminManager.findAll();
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        // assertEquals(2, listOfAdmins.size());
    }

    // Update tests

    @Test
    public void adminManagerUpdateAdminTestPositive() throws AdminManagerUpdateException, AdminManagerReadException  {
        String adminLoginBefore = adminNo1.getClientLogin();
        String adminPasswordBefore = adminNo1.getClientPassword();
        String newAdminLogin = "OtherNewLoginNo1";
        String newAdminPassword = "OtherNewPasswordNo1";
        adminNo1.setClientLogin(newAdminLogin);
        adminNo1.setClientPassword(newAdminPassword);
        adminManager.update(adminNo1);
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
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
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithLoginLengthEqualTo8TestNegative() throws AdminManagerReadException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminManager.update(adminNo1));
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithLoginLengthEqualTo20TestNegative() throws AdminManagerReadException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminManager.update(adminNo1));
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithNullPasswordTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = null;
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithEmptyPasswordTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordTooShortTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfdd";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordTooLongTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordLengthEqualTo8TestNegative() throws AdminManagerReadException {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfddd";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminManager.update(adminNo1));
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordLengthEqualTo40TestNegative() throws AdminManagerReadException {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertDoesNotThrow(() -> adminManager.update(adminNo1));
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertEquals(adminLogin, foundAdmin.getClientLogin());
        assertEquals(adminPassword, foundAdmin.getClientPassword());
    }

    @Test
    public void adminManagerUpdateAdminWithPasswordThatViolatesRegExTestNegative() {
        String adminLogin = "SomeOtherLoginNo2";
        String adminPassword = "Some Invalid Password";
        adminNo1.setClientLogin(adminLogin);
        adminNo1.setClientPassword(adminPassword);
        assertThrows(AdminManagerUpdateException.class, () -> adminManager.update(adminNo1));
    }

    // Delete tests

    @Test
    public void adminManagerDeleteAdminTestPositive() throws AdminManagerReadException, AdminManagerDeleteException {
        UUID removedAdminUUID = adminNo1.getClientID();
        Admin foundAdmin = adminManager.findByUUID(removedAdminUUID);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        adminManager.delete(removedAdminUUID);
        assertThrows(AdminManagerReadException.class, () -> adminManager.findByUUID(removedAdminUUID));
    }

    @Test
    public void adminManagerDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminManagerDeleteException.class, () -> adminManager.delete(admin.getClientID()));
    }

    // Activate tests

    @Test
    public void adminManagerActivateAdminTestPositive() throws GeneralAdminManagerException {
        adminManager.deactivate(adminNo1.getClientID());

        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isClientStatusActive());
        adminManager.activate(adminNo1.getClientID());
        foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isClientStatusActive());
    }

    @Test
    public void adminManagerDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminManagerActivationException.class, () -> adminManager.activate(admin.getClientID()));
    }

    // Deactivate tests

    @Test
    public void adminManagerDeactivateAdminTestPositive() throws GeneralAdminManagerException {
        Admin foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        assertTrue(foundAdmin.isClientStatusActive());
        adminManager.deactivate(adminNo1.getClientID());
        foundAdmin = adminManager.findByUUID(adminNo1.getClientID());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isClientStatusActive());
    }

    @Test
    public void adminManagerActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminManagerDeactivationException.class, () -> adminManager.deactivate(admin.getClientID()));
    }
}
