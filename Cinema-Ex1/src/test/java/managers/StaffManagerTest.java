package managers;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.*;
import pl.pas.gr3.cinema.managers.implementations.StaffManager;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StaffManagerTest {

    private final static String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(StaffManagerTest.class);
    private static ClientRepository clientRepository;
    private static StaffManager staffManager;

    private Staff staffNo1;
    private Staff staffNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        staffManager = new StaffManager(clientRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            staffNo1 = staffManager.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffManager.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        } catch (StaffManagerCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Staff> listOfStaffs = staffManager.findAll();
            for (Staff staff : listOfStaffs) {
                staffManager.delete(staff.getClientID());
            }
        } catch (StaffManagerReadException | StaffManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        staffManager.close();
    }

    // Create tests

    @Test
    public void staffManagerCreateStaffTestPositive() throws StaffManagerCreateException {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffManager.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithNullLoginThatTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithEmptyLoginThatTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginTooShortThatTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginTooLongThatTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginLengthEqualTo8ThatTestPositive() throws StaffManagerCreateException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffManager.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithLoginLengthEqualTo20ThatTestNegative() throws StaffManagerCreateException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffManager.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithLoginThatDoesNotMeetRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithLoginThatIsAlreadyInTheDatabaseTestNegative() {
        String staffLogin = staffNo1.getClientLogin();
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithNullPasswordThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = null;
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithEmptyPasswordThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithPasswordTooShortThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfdd";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithPasswordTooLongThatTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    @Test
    public void staffManagerCreateStaffWithPasswordLengthEqualTo8ThatTestNegative() throws StaffManagerCreateException {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfddd";
        Staff staff = staffManager.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithPasswordLengthEqualTo20ThatTestNegative() throws StaffManagerCreateException {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        Staff staff = staffManager.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getClientLogin());
        assertEquals(staffPassword, staff.getClientPassword());
    }

    @Test
    public void staffManagerCreateStaffWithPasswordThatDoesNotMeetRegExTestNegative() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "Some Invalid Password";
        assertThrows(StaffManagerCreateException.class, () -> staffManager.create(staffLogin, staffPassword));
    }

    // Read tests

    @Test
    public void staffManagerFindStaffByIDTestPositive() throws StaffManagerReadException {
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void staffManagerFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(StaffManagerReadException.class, () -> staffManager.findByUUID(staff.getClientID()));
    }

    @Test
    public void staffManagerFindStaffByLoginTestPositive() throws StaffManagerReadException {
        Staff foundStaff = staffManager.findByLogin(staffNo1.getClientLogin());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    public void staffManagerFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(StaffManagerReadException.class, () -> staffManager.findByLogin(staff.getClientLogin()));
    }

    @Test
    public void staffManagerFindFindStaffsMatchingLoginTestPositive() throws StaffManagerCreateException, StaffManagerReadException{
        staffManager.create("NewStaffLogin", "NewStaffPassword");
        List<Staff> listOfStaffs = staffManager.findAllMatchingLogin("New");
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(1, listOfStaffs.size());
    }

    @Test
    public void staffManagerFindFindStaffsTestPositive() throws StaffManagerReadException {
        List<Staff> listOfStaffs = staffManager.findAll();
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(2, listOfStaffs.size());
    }

    // Update tests

    @Test
    public void staffManagerUpdateStaffTestPositive() throws StaffManagerUpdateException, StaffManagerReadException {
        String staffLoginBefore = staffNo1.getClientLogin();
        String staffPasswordBefore = staffNo1.getClientPassword();
        String newStaffLogin = "OtherNewLoginNo1";
        String newStaffPassword = "OtherNewPasswordNo1";
        staffNo1.setClientLogin(newStaffLogin);
        staffNo1.setClientPassword(newStaffPassword);
        staffManager.update(staffNo1);
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
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
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithLoginLengthEqualTo8TestPositive() throws StaffManagerReadException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffManager.update(staffNo1));
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithLoginLengthEqualTo20TestPositive() throws StaffManagerReadException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffManager.update(staffNo1));
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithNullPasswordTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = null;
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithEmptyPasswordTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordTooShortTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "ddddfdd";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordTooLongTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordLengthEqualTo8TestPositive() throws StaffManagerReadException {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffManager.update(staffNo1));
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordLengthEqualTo20TestPositive() throws StaffManagerReadException {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertDoesNotThrow(() -> staffManager.update(staffNo1));
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertEquals(staffLogin, foundStaff.getClientLogin());
        assertEquals(staffPassword, foundStaff.getClientPassword());
    }

    @Test
    public void staffManagerUpdateStaffWithPasswordThatViolatesRegExTestNegative() {
        String staffLogin = "SomeOtherLoginNo2";
        String staffPassword = "Some Invalid Password";
        staffNo1.setClientLogin(staffLogin);
        staffNo1.setClientPassword(staffPassword);
        assertThrows(StaffManagerUpdateException.class, () -> staffManager.update(staffNo1));
    }

    // Delete tests

    @Test
    public void staffManagerDeleteStaffTestPositive() throws StaffManagerReadException, StaffManagerDeleteException {
        UUID removedStaffUUID = staffNo1.getClientID();
        Staff foundStaff = staffManager.findByUUID(removedStaffUUID);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        staffManager.delete(removedStaffUUID);
        assertThrows(StaffManagerReadException.class, () -> staffManager.findByUUID(removedStaffUUID));
    }

    @Test
    public void staffManagerDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffManagerDeleteException.class, () -> staffManager.delete(staff.getClientID()));
    }

    // Activate tests

    @Test
    public void staffManagerActivateStaffTestPositive() throws StaffManagerDeactivationException, StaffManagerActivationException, StaffManagerReadException {
        staffManager.deactivate(staffNo1.getClientID());

        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isClientStatusActive());
        staffManager.activate(staffNo1.getClientID());
        foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isClientStatusActive());
    }

    @Test
    public void staffManagerActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffManagerActivationException.class, () -> staffManager.activate(staff.getClientID()));
    }

    // Deactivate tests

    @Test
    public void staffManagerDeactivateStaffTestPositive() throws StaffManagerDeactivationException, StaffManagerReadException {
        Staff foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        assertTrue(foundStaff.isClientStatusActive());
        staffManager.deactivate(staffNo1.getClientID());
        foundStaff = staffManager.findByUUID(staffNo1.getClientID());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isClientStatusActive());
    }

    @Test
    public void staffManagerDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(StaffManagerDeactivationException.class, () -> staffManager.deactivate(staff.getClientID()));
    }
}
