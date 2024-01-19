package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.dto.users.AdminDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminDTOTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static boolean statusActiveNo1;

    private AdminDTO adminDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "SomeLoginNo1";
        statusActiveNo1 = true;
    }

    @BeforeEach
    public void initAdminDTO() {
        adminDTO = new AdminDTO(uuidNo1, loginNo1, statusActiveNo1);
    }

    @Test
    public void adminDTONoArgsConstructorTestPositive() {
        AdminDTO testAdminDTO = new AdminDTO();
        assertNotNull(testAdminDTO);
    }

    @Test
    public void adminDTOAllArgsConstructorAndGettersTestPositive() {
        AdminDTO testAdminDTO = new AdminDTO(uuidNo1, loginNo1, statusActiveNo1);
        assertNotNull(testAdminDTO);
        assertEquals(uuidNo1, testAdminDTO.getAdminID());
        assertEquals(loginNo1, testAdminDTO.getAdminLogin());
        assertEquals(statusActiveNo1, testAdminDTO.isAdminStatusActive());
    }

    @Test
    public void adminDTOIDSetterTestPositive() {
        UUID oldUUID = adminDTO.getAdminID();
        assertNotNull(oldUUID);
        UUID newUUID = UUID.randomUUID();
        assertNotNull(newUUID);
        assertNotEquals(newUUID, oldUUID);
        adminDTO.setAdminID(newUUID);
        assertNotNull(adminDTO.getAdminID());
        assertEquals(newUUID, adminDTO.getAdminID());
        assertNotEquals(oldUUID, adminDTO.getAdminID());
    }

    @Test
    public void adminDTOLoginSetterTestPositive() {
        String oldLogin = adminDTO.getAdminLogin();
        assertNotNull(oldLogin);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        assertNotEquals(oldLogin, newLogin);
        adminDTO.setAdminLogin(newLogin);
        assertNotNull(adminDTO.getAdminLogin());
        assertEquals(newLogin, adminDTO.getAdminLogin());
        assertNotEquals(oldLogin, adminDTO.getAdminLogin());
    }

    @Test
    public void adminDTOStatusActiveSetterTestPositive() {
        boolean oldStatusActive = adminDTO.isAdminStatusActive();
        boolean newStatusActive = !oldStatusActive;
        adminDTO.setAdminStatusActive(newStatusActive);
        assertEquals(newStatusActive, adminDTO.isAdminStatusActive());
        assertNotEquals(oldStatusActive, adminDTO.isAdminStatusActive());
    }
}