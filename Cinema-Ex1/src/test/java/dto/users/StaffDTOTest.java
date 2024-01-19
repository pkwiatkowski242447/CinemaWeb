package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.dto.users.StaffDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StaffDTOTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static boolean statusActiveNo1;

    private StaffDTO staffDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "SomeLoginNo1";
        statusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeStaffDTO() {
        staffDTO = new StaffDTO(uuidNo1, loginNo1, statusActiveNo1);
    }

    @Test
    public void staffDTONoArgsConstructorTestPositive() {
        StaffDTO testStaffDTO = new StaffDTO();
        assertNotNull(testStaffDTO);
    }

    @Test
    public void staffDTOAllArgsConstructorAndGettersTestPositive() {
        StaffDTO testStaffDTO = new StaffDTO(uuidNo1, loginNo1, statusActiveNo1);
        assertNotNull(testStaffDTO);
        assertEquals(uuidNo1, testStaffDTO.getStaffID());
        assertEquals(loginNo1, testStaffDTO.getStaffLogin());
        assertEquals(statusActiveNo1, testStaffDTO.isStaffStatusActive());
    }

    @Test
    public void staffDTOIDSetterTestPositive() {
        UUID oldUUID = staffDTO.getStaffID();
        assertNotNull(oldUUID);
        UUID newUUID = UUID.randomUUID();
        assertNotNull(newUUID);
        assertNotEquals(newUUID, oldUUID);
        staffDTO.setStaffID(newUUID);
        assertNotNull(staffDTO.getStaffID());
        assertEquals(newUUID, staffDTO.getStaffID());
        assertNotEquals(oldUUID, staffDTO.getStaffID());
    }

    @Test
    public void staffDTOLoginSetterTestPositive() {
        String oldLogin = staffDTO.getStaffLogin();
        assertNotNull(oldLogin);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        assertNotEquals(oldLogin, newLogin);
        staffDTO.setStaffLogin(newLogin);
        assertNotNull(staffDTO.getStaffLogin());
        assertEquals(newLogin, staffDTO.getStaffLogin());
        assertNotEquals(oldLogin, staffDTO.getStaffLogin());
    }

    @Test
    public void staffDTOStatusActiveSetterTestPositive() {
        boolean oldStatusActive = staffDTO.isStaffStatusActive();
        boolean newStatusActive = !oldStatusActive;
        staffDTO.setStaffStatusActive(newStatusActive);
        assertEquals(newStatusActive, staffDTO.isStaffStatusActive());
        assertNotEquals(oldStatusActive, staffDTO.isStaffStatusActive());
    }
}
