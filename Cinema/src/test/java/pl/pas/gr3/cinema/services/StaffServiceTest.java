package pl.pas.gr3.cinema.services;

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
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.service.impl.StaffServiceImpl;
import pl.pas.gr3.cinema.entity.account.Staff;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class StaffServiceTest {

    private static final String DATABASE_NAME = "test";

    private static AccountRepositoryImpl accountRepository;
    private static StaffServiceImpl staffService;

    private Staff staffNo1;
    private Staff staffNo2;

    private AccountMapper accountMapper;

    @BeforeAll
    static void initialize() {
        accountRepository = new AccountRepositoryImpl(DATABASE_NAME);
        staffService = new StaffServiceImpl(accountRepository);
    }

    @BeforeEach
    void initializeSampleData() {
        accountMapper = Mappers.getMapper(AccountMapper.class);
        ReflectionTestUtils.setField(accountRepository, "accountMapper", accountMapper);

        this.clearTestData();
        try {
            staffNo1 = staffService.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffService.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
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
            List<Staff> staffs = staffService.findAll();
            staffs.forEach(staff -> staffService.delete(staff.getId()));
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
    void staffServiceAllArgsConstructorTestPositive() {
        StaffServiceImpl testStaffService = new StaffServiceImpl(accountRepository);
        assertNotNull(testStaffService);
    }

    // Create tests

    @Test
    void staffServiceCreateStaffTestPositive() {
        String staffLogin = "SomeOtherLoginNo1";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getLogin());
        assertEquals(staffPassword, staff.getPassword());
    }

    @Test
    void staffServiceCreateStaffWithNullLoginThatTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    void staffServiceCreateStaffWithEmptyLoginThatTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    void staffServiceCreateStaffWithLoginTooShortThatTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    void staffServiceCreateStaffWithLoginTooLongThatTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    @Test
    void staffServiceCreateStaffWithLoginLengthEqualTo8ThatTestPositive() {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getLogin());
        assertEquals(staffPassword, staff.getPassword());
    }

    @Test
    void staffServiceCreateStaffWithLoginLengthEqualTo20ThatTestNegative() {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo1";
        Staff staff = staffService.create(staffLogin, staffPassword);
        assertNotNull(staff);
        assertEquals(staffLogin, staff.getLogin());
        assertEquals(staffPassword, staff.getPassword());
    }

    @Test
    void staffServiceCreateStaffWithLoginThatDoesNotMeetRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> staffService.create(staffLogin, staffPassword));
    }

    // Read tests

    @Test
    void staffServiceFindStaffByIDTestPositive() {
        Staff foundStaff = staffService.findByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    void staffServiceFindStaffByIDThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class, () -> staffService.findByUUID(staff.getId()));
    }

    @Test
    void staffServiceFindStaffByLoginTestPositive() {
        Staff foundStaff = staffService.findByLogin(staffNo1.getLogin());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
    }

    @Test
    void staffServiceFindStaffByLoginThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class, () -> staffService.findByLogin(staff.getLogin()));
    }

    @Test
    void staffServiceFindFindStaffsMatchingLoginTestPositive() {
        staffService.create("NewStaffLogin", "NewStaffPassword");
        List<Staff> listOfStaffs = staffService.findAllMatchingLogin("New");
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(1, listOfStaffs.size());
    }

    @Test
    void staffServiceFindFindStaffsTestPositive(){
        List<Staff> listOfStaffs = staffService.findAll();
        assertNotNull(listOfStaffs);
        assertFalse(listOfStaffs.isEmpty());
        assertEquals(2, listOfStaffs.size());
    }

    // Update tests

    @Test
    void staffServiceUpdateStaffTestPositive() {
        String staffLoginBefore = staffNo1.getLogin();
        String staffPasswordBefore = staffNo1.getPassword();
        String newStaffLogin = "OtherNewLoginNo1";
        String newStaffPassword = "OtherNewPasswordNo1";
        staffNo1.setLogin(newStaffLogin);
        staffNo1.setPassword(newStaffPassword);
        staffService.update(staffNo1);
        Staff foundStaff = staffService.findByUUID(staffNo1.getId());
        String staffLoginAfter =  foundStaff.getLogin();
        String staffPasswordAfter = foundStaff.getPassword();
        assertNotNull(staffLoginAfter);
        assertNotNull(staffPasswordAfter);
        assertEquals(newStaffLogin, staffLoginAfter);
        assertEquals(newStaffPassword, staffPasswordAfter);
        assertNotEquals(staffLoginBefore, staffLoginAfter);
        assertNotEquals(staffPasswordBefore, staffPasswordAfter);
    }

    @Test
    void staffServiceUpdateStaffWithNullLoginTestNegative() {
        String staffLogin = null;
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertThrows(AccountUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    void staffServiceUpdateStaffWithEmptyLoginTestNegative() {
        String staffLogin = "";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertThrows(AccountUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    void staffServiceUpdateStaffWithLoginTooShortTestNegative() {
        String staffLogin = "ddddfdd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertThrows(AccountUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    void staffServiceUpdateStaffWithLoginTooLongTestNegative() {
        String staffLogin = "ddddfddddfddddfddddfd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertThrows(AccountUpdateException.class, () -> staffService.update(staffNo1));
    }

    @Test
    void staffServiceUpdateStaffWithLoginLengthEqualTo8TestPositive() {
        String staffLogin = "ddddfddd";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getId());
        assertEquals(staffLogin, foundStaff.getLogin());
        assertEquals(staffPassword, foundStaff.getPassword());
    }

    @Test
    void staffServiceUpdateStaffWithLoginLengthEqualTo20TestPositive() {
        String staffLogin = "ddddfddddfddddfddddf";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertDoesNotThrow(() -> staffService.update(staffNo1));
        Staff foundStaff = staffService.findByUUID(staffNo1.getId());
        assertEquals(staffLogin, foundStaff.getLogin());
        assertEquals(staffPassword, foundStaff.getPassword());
    }

    @Test
    void staffServiceUpdateStaffWithLoginThatViolatesRegExTestNegative() {
        String staffLogin = "Some Invalid Login";
        String staffPassword = "SomeOtherPasswordNo2";
        staffNo1.setLogin(staffLogin);
        staffNo1.setPassword(staffPassword);
        assertThrows(AccountUpdateException.class, () -> staffService.update(staffNo1));
    }

    // Delete tests

    @Test
    void staffServiceDeleteStaffTestPositive() {
        UUID removedStaffUUID = staffNo1.getId();
        Staff foundStaff = staffService.findByUUID(removedStaffUUID);
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        staffService.delete(removedStaffUUID);
        assertThrows(AccountNotFoundException.class, () -> staffService.findByUUID(removedStaffUUID));
    }

    @Test
    void staffServiceDeleteStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class, () -> staffService.delete(staff.getId()));
    }

    // Activate tests

    @Test
    void staffServiceActivateStaffTestPositive() {
        staffService.deactivate(staffNo1.getId());

        Staff foundStaff = staffService.findByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isActive());
        staffService.activate(staffNo1.getId());
        foundStaff = staffService.findByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertTrue(foundStaff.isActive());
    }

    @Test
    void staffServiceActivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class, () -> staffService.activate(staff.getId()));
    }

    // Deactivate tests

    @Test
    void staffServiceDeactivateStaffTestPositive() {
        Staff foundStaff = staffService.findByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertEquals(staffNo1, foundStaff);
        assertTrue(foundStaff.isActive());
        staffService.deactivate(staffNo1.getId());
        foundStaff = staffService.findByUUID(staffNo1.getId());
        assertNotNull(foundStaff);
        assertFalse(foundStaff.isActive());
    }

    @Test
    void staffServiceDeactivateStaffThatIsNotInTheDatabaseTestNegative() {
        Staff staff = new Staff(UUID.randomUUID(), "SomeOtherStaffLoginNo3", "SomeOtherStaffPasswordNo3");
        assertNotNull(staff);
        assertThrows(AccountNotFoundException.class, () -> staffService.deactivate(staff.getId()));
    }
}
