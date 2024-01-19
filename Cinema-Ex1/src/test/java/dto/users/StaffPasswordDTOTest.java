package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.dto.users.StaffPasswordDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StaffPasswordDTOTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static String passwordNo1;
    private static boolean statusActiveNo1;

    private StaffPasswordDTO staffPasswordDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "SomeLoginNo1";
        passwordNo1 = "SomePasswordNo1";
        statusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeStaffPasswordDTO() {
        staffPasswordDTO = new StaffPasswordDTO(uuidNo1, loginNo1, passwordNo1, statusActiveNo1);
    }

    @Test
    public void staffPasswordDTONoArgsConstructorTestPositive() {
        StaffPasswordDTO testStaffPasswordDTO = new StaffPasswordDTO();
        assertNotNull(testStaffPasswordDTO);
    }

    @Test
    public void staffPasswordDTOAllArgsConstructorTestPositive() {
        StaffPasswordDTO testStaffPasswordDTO = new StaffPasswordDTO(uuidNo1, loginNo1, passwordNo1, statusActiveNo1);
        assertNotNull(testStaffPasswordDTO);
        assertEquals(uuidNo1, testStaffPasswordDTO.getStaffID());
        assertEquals(loginNo1, testStaffPasswordDTO.getStaffLogin());
        assertEquals(passwordNo1, testStaffPasswordDTO.getStaffPassword());
        assertEquals(statusActiveNo1, testStaffPasswordDTO.isStaffStatusActive());
    }

    @Test
    public void staffPasswordDTOIDSetterTestPositive() {
        UUID oldUUID = staffPasswordDTO.getStaffID();
        assertNotNull(oldUUID);
        UUID newUUID = UUID.randomUUID();
        assertNotNull(newUUID);
        assertNotEquals(newUUID, oldUUID);
        staffPasswordDTO.setStaffID(newUUID);
        assertNotNull(staffPasswordDTO.getStaffID());
        assertEquals(newUUID, staffPasswordDTO.getStaffID());
        assertNotEquals(oldUUID, staffPasswordDTO.getStaffID());
    }

    @Test
    public void staffPasswordDTOLoginSetterTestPositive() {
        String oldLogin = staffPasswordDTO.getStaffLogin();
        assertNotNull(oldLogin);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        assertNotEquals(oldLogin, newLogin);
        staffPasswordDTO.setStaffLogin(newLogin);
        assertNotNull(staffPasswordDTO.getStaffLogin());
        assertEquals(newLogin, staffPasswordDTO.getStaffLogin());
        assertNotEquals(oldLogin, staffPasswordDTO.getStaffLogin());
    }

    @Test
    public void staffPasswordDTOPasswordSetterTestPositive() {
        String oldPassword = staffPasswordDTO.getStaffPassword();
        assertNotNull(oldPassword);
        String newPassword = "NewPasswordNo1";
        assertNotNull(newPassword);
        assertNotEquals(oldPassword, newPassword);
        staffPasswordDTO.setStaffPassword(newPassword);
        assertNotNull(staffPasswordDTO.getStaffPassword());
        assertEquals(newPassword, staffPasswordDTO.getStaffPassword());
        assertNotEquals(oldPassword, staffPasswordDTO.getStaffPassword());
    }

    @Test
    public void staffPasswordDTOStatusActiveSetterTestPositive() {
        boolean oldStatusActive = staffPasswordDTO.isStaffStatusActive();
        boolean newStatusActive = !oldStatusActive;
        staffPasswordDTO.setStaffStatusActive(newStatusActive);
        assertEquals(newStatusActive, staffPasswordDTO.isStaffStatusActive());
        assertNotEquals(oldStatusActive, staffPasswordDTO.isStaffStatusActive());
    }
}