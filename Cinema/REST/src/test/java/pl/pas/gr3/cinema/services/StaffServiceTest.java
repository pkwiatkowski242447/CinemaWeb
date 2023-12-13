package pl.pas.gr3.cinema.services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.*;
import pl.pas.gr3.cinema.services.implementations.StaffService;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StaffServiceTest {

    private final static String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceTest.class);
    private static ClientRepository clientRepository;
    private static StaffService staffService;

    private Staff staffNo1;
    private Staff staffNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        staffService = new StaffService(clientRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            staffNo1 = staffService.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffService.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        } catch (StaffServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Staff> listOfStaffs = staffService.findAll();
            for (Staff staff : listOfStaffs) {
                staffService.delete(staff.getClientID());
            }
        } catch (StaffServiceReadException | StaffServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        clientRepository.close();
    }

    // Create tests

    @Test
    public void staffManagerCreateStaffTestPositive() throws StaffServiceCreateException {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithNullLoginThatTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithEmptyLoginThatTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginTooShortThatTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginTooLongThatTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginLengthEqualTo8ThatTestPositive() throws StaffServiceCreateException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithLoginLengthEqualTo20ThatTestNegative() throws StaffServiceCreateException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithLoginThatDoesNotMeetRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginThatIsAlreadyInTheDatabaseTestNegative() {
        String staffLogin = staffNo1.getClientLogin();
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithNullPasswordThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = null;
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithEmptyPasswordThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithPasswordTooShortThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfdd";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithPasswordTooLongThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithPasswordLengthEqualTo8ThatTestNegative() throws StaffServiceCreateException {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfddd";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithPasswordLengthEqualTo20ThatTestNegative() throws StaffServiceCreateException {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithPasswordThatDoesNotMeetRegExTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "Some Invalid Password";
        assertThrows(StaffServiceCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    // Read tests

    @Test
    public void staffManagerFindStaffByIDTestPositive() throws StaffServiceReadException {
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void staffManagerFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByUUID(staff.getClientID()));
    }

    @Test
    public void staffManagerFindStaffByLoginTestPositive() throws StaffServiceReadException {
        Staff foundStaff = staffService.findByLogin(staffNo1.getClientLogin());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void staffManagerFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(StaffServiceStaffNotFoundException.class, () -> staffService.findByLogin(staff.getClientLogin()));
    }

    @Test
    public void staffManagerFindFindStaffsMatchingLoginTestPositive() throws StaffServiceCreateException, StaffServiceReadException {
        staffService.create("NewStaffLogin", "NewStaffPassword");
        List<Staff> listOfStaffs = staffService.findAllMatchingLogin("New");
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(1, listOfStaffs.size());
    }

    @Test
    public void staffManagerFindFindStaffsTestPositive() throws StaffServiceReadException {
        List<Staff> listOfStaffs = staffService.findAll();
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(2, listOfStaffs.size());
    }

    // Update tests

    @Test
    public void staffManagerUpdateStaffTestPositive() throws StaffServiceUpdateException, StaffServiceReadException {
        String staffLoginBefore = staffNo1.getClientLogin();
        String staffPasswordBefore = staffNo1.getClientPassword();
        String newStaffLogin = "OtherNewLoginNo1";
        String newStaffPassword = "OtherNewPasswordNo1";
        staffNo1.setClientLogin(newStaffLogin);
        staffNo1.setClientPassword(newStaffPassword);
        staffService.update(staffNo1);
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        String staffLoginAfter =  foundStaff.getClientLogin();
        String staffPasswordAfter = foundStaff.getClientPassword();
        assertNotNull(staffLoginAfter);
        assertNotNull(staffPasswordAfter);
        assertEquals(newStaffLogin, staffLoginAfter);
        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    public void staffManagerUpdateStaffWithNullLoginTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithLoginLengthEqualTo8TestPositive() throws StaffServiceReadException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithLoginLengthEqualTo20TestPositive() throws StaffServiceReadException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithNullPasswordTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = null;
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithEmptyPasswordTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordTooShortTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "ddddfdd";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordTooLongTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordLengthEqualTo8TestPositive() throws StaffServiceReadException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordLengthEqualTo20TestPositive() throws StaffServiceReadException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordThatViolatesRegExTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "Some Invalid Password";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffServiceUpdateException.class, () -> staffService.update(staffNo1));
    }

    // Delete tests

    @Test
    public void staffManagerDeleteStaffTestPositive() throws StaffServiceReadException, StaffServiceDeleteException {
        UUID removedStaffUUID = staffNo1.getClientID();
        Staff foundStaff = staffService.findByUUID(removedStaffUUID);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        staffService.delete(removedStaffUUID);
        assertThrows(StaffServiceReadException.class, () -> staffService.findByUUID(removedStaffUUID));
    }

    @Test
    public void staffManagerDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffServiceDeleteException.class, () -> staffService.delete(staff.getClientID()));
    }

    // Activate tests

    @Test
    public void staffManagerActivateStaffTestPositive() throws StaffServiceDeactivationException, StaffServiceActivationException, StaffServiceReadException {
        staffService.deactivate(staffNo1.getClientID());

        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isClientStatusActive());
        staffService.activate(staffNo1.getClientID());
        foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isClientStatusActive());
    }

    @Test
    public void staffManagerActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffServiceActivationException.class, () -> staffService.activate(staff.getClientID()));
    }

    // Deactivate tests

    @Test
    public void staffManagerDeactivateStaffTestPositive() throws StaffServiceDeactivationException, StaffServiceReadException {
        Staff foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        assertTrue(foundStaff.isClientStatusActive());
        staffService.deactivate(staffNo1.getClientID());
        foundStaff = staffService.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isClientStatusActive());
    }

    @Test
    public void staffManagerDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffServiceDeactivationException.class, () -> staffService.deactivate(staff.getClientID()));
    }
}
