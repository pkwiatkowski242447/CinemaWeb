package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.dto.update.AdminPasswordDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AdminPasswordDTOTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static String passwordNo1;
    private static boolean statusActiveNo1;

    private AdminPasswordDTO adminPasswordDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "SomeLoginNo1";
        passwordNo1 = "SomePasswordNo1";
        statusActiveNo1 = true;
    }

    @BeforeEach
    public void initAdminPasswordDTO() {
        adminPasswordDTO = new AdminPasswordDTO(uuidNo1, loginNo1, passwordNo1, statusActiveNo1);
    }

    @Test
    public void adminPasswordDTONoArgsConstructorTestPositive() {
        AdminPasswordDTO testAdminPasswordDTO = new AdminPasswordDTO();
        assertNotNull(testAdminPasswordDTO);
    }

    @Test
    public void adminPasswordDTOAllArgsConstructorAndGettersTestPositive() {
        AdminPasswordDTO testAdminPasswordDTO = new AdminPasswordDTO(uuidNo1, loginNo1, passwordNo1, statusActiveNo1);
        assertNotNull(testAdminPasswordDTO);
        assertEquals(uuidNo1, testAdminPasswordDTO.getAdminID());
        assertEquals(loginNo1, testAdminPasswordDTO.getAdminLogin());
        assertEquals(passwordNo1, testAdminPasswordDTO.getAdminPassword());
        assertEquals(statusActiveNo1, testAdminPasswordDTO.isAdminStatusActive());
    }

    @Test
    public void adminPasswordDTOIDSetterTestPositive() {
        UUID oldUUID = adminPasswordDTO.getAdminID();
        assertNotNull(oldUUID);
        UUID newUUID = UUID.randomUUID();
        assertNotNull(newUUID);
        assertNotEquals(newUUID, oldUUID);
        adminPasswordDTO.setAdminID(newUUID);
        assertNotNull(adminPasswordDTO.getAdminID());
        assertEquals(newUUID, adminPasswordDTO.getAdminID());
        assertNotEquals(oldUUID, adminPasswordDTO.getAdminID());
    }

    @Test
    public void adminPasswordDTOLoginSetterTestPositive() {
        String oldLogin = adminPasswordDTO.getAdminLogin();
        assertNotNull(oldLogin);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        assertNotEquals(oldLogin, newLogin);
        adminPasswordDTO.setAdminLogin(newLogin);
        assertNotNull(adminPasswordDTO.getAdminLogin());
        assertEquals(newLogin, adminPasswordDTO.getAdminLogin());
        assertNotEquals(oldLogin, adminPasswordDTO.getAdminLogin());
    }

    @Test
    public void adminPasswordDTOPasswordSetterTestPositive() {
        String oldPassword = adminPasswordDTO.getAdminPassword();
        assertNotNull(oldPassword);
        String newPassword = "NewPasswordNo1";
        assertNotNull(newPassword);
        assertNotEquals(oldPassword, newPassword);
        adminPasswordDTO.setAdminPassword(newPassword);
        assertNotNull(adminPasswordDTO.getAdminPassword());
        assertEquals(newPassword, adminPasswordDTO.getAdminPassword());
        assertNotEquals(oldPassword, adminPasswordDTO.getAdminPassword());
    }

    @Test
    public void adminPasswordDTOStatusActiveSetterTestPositive() {
        boolean oldStatusActive = adminPasswordDTO.isAdminStatusActive();
        boolean newStatusActive = !oldStatusActive;
        adminPasswordDTO.setAdminStatusActive(newStatusActive);
        assertEquals(newStatusActive, adminPasswordDTO.isAdminStatusActive());
        assertNotEquals(oldStatusActive, adminPasswordDTO.isAdminStatusActive());
    }
}