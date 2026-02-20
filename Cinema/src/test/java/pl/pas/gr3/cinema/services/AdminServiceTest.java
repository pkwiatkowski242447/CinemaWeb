package pl.pas.gr3.cinema.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.service.impl.AdminServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class AdminServiceTest {

    private static final String DATABASE_NAME = "test";
    private static AccountRepositoryImpl accountRepository;
    private static AdminServiceImpl adminService;

    private Admin adminNo1;
    private Admin adminNo2;

    @BeforeAll
    static void initialize() {
        accountRepository = new AccountRepositoryImpl(DATABASE_NAME);
        adminService = new AdminServiceImpl(accountRepository);
    }

    @BeforeEach
    void initializeSampleData() {
        this.clearTestData();
        try {
            adminNo1 = adminService.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminService.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterEach
    void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Admin> admins = adminService.findAll();
            admins.forEach(admin -> adminService.delete(admin.getId()));
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterAll
    static void destroy() {
        accountRepository.close();
    }

    // Constructor tests

    @Test
    void adminServiceAllArgsConstructorTestPositive() {
        AdminServiceImpl testAdminService = new AdminServiceImpl(accountRepository);
        assertNotNull(testAdminService);
    }

    // Create tests

    @Test
    void adminServiceCreateAdminTestPositive() throws AdminServiceCreateException {
        String adminLogin = "SomeOtherLoginNo1";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getLogin());
        assertEquals(adminPassword, admin.getPassword());
    }

    @Test
    void adminServiceCreateAdminWithNullLoginThatTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    void adminServiceCreateAdminWithEmptyLoginThatTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    void adminServiceCreateAdminWithLoginTooShortThatTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    void adminServiceCreateAdminWithLoginTooLongThatTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    @Test
    void adminServiceCreateAdminWithLoginLengthEqualTo8ThatTestPositive() throws AdminServiceCreateException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getLogin());
        assertEquals(adminPassword, admin.getPassword());
    }

    @Test
    void adminServiceCreateAdminWithLoginLengthEqualTo20ThatTestNegative() throws AdminServiceCreateException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo1";
        Admin admin = adminService.create(adminLogin, adminPassword);
        assertNotNull(admin);
        assertEquals(adminLogin, admin.getLogin());
        assertEquals(adminPassword, admin.getPassword());
    }

    @Test
    void adminServiceCreateAdminWithLoginThatDoesNotMeetRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo1";
        assertThrows(AdminServiceCreateException.class, () -> adminService.create(adminLogin, adminPassword));
    }

    // Read tests

    @Test
    void adminServiceFindAdminByIDTestPositive() throws AdminServiceReadException {
        Admin foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    void adminServiceFindAdminByIDThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByUUID(admin.getId()));
    }

    @Test
    void adminServiceFindAdminByLoginTestPositive() throws AdminServiceReadException {
        Admin foundAdmin = adminService.findByLogin(adminNo1.getLogin());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
    }

    @Test
    void adminServiceFindAdminByLoginThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(admin);
        assertThrows(AdminServiceAdminNotFoundException.class, () -> adminService.findByLogin(admin.getLogin()));
    }

    @Test
    void adminServiceFindAllAdminsMatchingLoginTestPositive() throws AdminServiceCreateException, AdminServiceReadException {
        adminService.create("NewAdminLogin", "NewAdminPassword");
        List<Admin> listOfAdmins = adminService.findAllMatchingLogin("New");
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        assertEquals(1, listOfAdmins.size());
    }

    @Test
    void adminServiceFindAllAdminTestPositive() throws AdminServiceReadException {
        List<Admin> listOfAdmins = adminService.findAll();
        assertNotNull(listOfAdmins);
        assertFalse(listOfAdmins.isEmpty());
        // assertEquals(2, listOfAdmins.size());
    }

    // Update tests

    @Test
    void adminServiceUpdateAdminTestPositive() throws AdminServiceUpdateException, AdminServiceReadException {
        String adminLoginBefore = adminNo1.getLogin();
        String adminPasswordBefore = adminNo1.getPassword();
        String newAdminLogin = "OtherNewLoginNo1";
        String newAdminPassword = "OtherNewPasswordNo1";
        adminNo1.setLogin(newAdminLogin);
        adminNo1.setPassword(newAdminPassword);
        adminService.update(adminNo1);
        Admin foundAdmin = adminService.findByUUID(adminNo1.getId());
        String adminLoginAfter =  foundAdmin.getLogin();
        String adminPasswordAfter = foundAdmin.getPassword();
        assertNotNull(adminLoginAfter);
        assertNotNull(adminPasswordAfter);
        assertEquals(newAdminLogin, adminLoginAfter);
        assertEquals(newAdminPassword, adminPasswordAfter);
        assertNotEquals(adminLoginBefore, adminLoginAfter);
        assertNotEquals(adminPasswordBefore, adminPasswordAfter);
    }

    @Test
    void adminServiceUpdateAdminWithNullLoginTestNegative() {
        String adminLogin = null;
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    void adminServiceUpdateAdminWithEmptyLoginTestNegative() {
        String adminLogin = "";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    void adminServiceUpdateAdminWithLoginTooShortTestNegative() {
        String adminLogin = "ddddfdd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    void adminServiceUpdateAdminWithLoginTooLongTestNegative() {
        String adminLogin = "ddddfddddfddddfddddfd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    @Test
    void adminServiceUpdateAdminWithLoginLengthEqualTo8TestNegative() throws AdminServiceReadException {
        String adminLogin = "ddddfddd";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertEquals(adminLogin, foundAdmin.getLogin());
        assertEquals(adminPassword, foundAdmin.getPassword());
    }

    @Test
    void adminServiceUpdateAdminWithLoginLengthEqualTo20TestNegative() throws AdminServiceReadException {
        String adminLogin = "ddddfddddfddddfddddf";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertDoesNotThrow(() -> adminService.update(adminNo1));
        Admin foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertEquals(adminLogin, foundAdmin.getLogin());
        assertEquals(adminPassword, foundAdmin.getPassword());
    }

    @Test
    void adminServiceUpdateAdminWithLoginThatViolatesRegExTestNegative() {
        String adminLogin = "Some Invalid Login";
        String adminPassword = "SomeOtherPasswordNo2";
        adminNo1.setLogin(adminLogin);
        adminNo1.setPassword(adminPassword);
        assertThrows(AdminServiceUpdateException.class, () -> adminService.update(adminNo1));
    }

    // Delete tests

    @Test
    void adminServiceDeleteAdminTestPositive() throws AdminServiceReadException, AdminServiceDeleteException {
        UUID removedAdminUUID = adminNo1.getId();
        Admin foundAdmin = adminService.findByUUID(removedAdminUUID);
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        adminService.delete(removedAdminUUID);
        assertThrows(AdminServiceReadException.class, () -> adminService.findByUUID(removedAdminUUID));
    }

    @Test
    void adminServiceDeleteAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceDeleteException.class, () -> adminService.delete(admin.getId()));
    }

    // Activate tests

    @Test
    void adminServiceActivateAdminTestPositive() throws GeneralAdminServiceException {
        adminService.deactivate(adminNo1.getId());

        Admin foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isActive());
        adminService.activate(adminNo1.getId());
        foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertTrue(foundAdmin.isActive());
    }

    @Test
    void adminServiceDeactivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceActivationException.class, () -> adminService.activate(admin.getId()));
    }

    // Deactivate tests

    @Test
    void adminServiceDeactivateAdminTestPositive() throws GeneralAdminServiceException {
        Admin foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertEquals(adminNo1, foundAdmin);
        assertTrue(foundAdmin.isActive());
        adminService.deactivate(adminNo1.getId());
        foundAdmin = adminService.findByUUID(adminNo1.getId());
        assertNotNull(foundAdmin);
        assertFalse(foundAdmin.isActive());
    }

    @Test
    void adminServiceActivateAdminThatIsNotInTheDatabaseTestNegative() {
        Admin admin = new Admin(UUID.randomUUID(), "SomeOtherAdminLoginNo3", "SomeOtherAdminPasswordNo3");
        assertNotNull(admin);
        assertThrows(AdminServiceDeactivationException.class, () -> adminService.deactivate(admin.getId()));
    }
}
