package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.dto.users.AdminInputDTO;

import static org.junit.jupiter.api.Assertions.*;

public class AdminInputDTOTest {

    private static String loginNo1;
    private static String passwordNo1;

    private AdminInputDTO adminInputDTO;

    @BeforeAll
    public static void init() {
        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
    }

    @BeforeEach
    public void initializeAdminInputDTO() {
        adminInputDTO = new AdminInputDTO(loginNo1, passwordNo1);
    }

    @Test
    public void createAdminInputDTONoArgsConstructorTestPositive() {
        AdminInputDTO testAdminInputDTO = new AdminInputDTO();
        assertNotNull(testAdminInputDTO);
    }

    @Test
    public void createAdminInputDTOAllArgsConstructorAndGettersTestPositive() {
        AdminInputDTO testAdminInputDTO = new AdminInputDTO(loginNo1, passwordNo1);
        assertNotNull(testAdminInputDTO);
        assertEquals(loginNo1, testAdminInputDTO.getAdminLogin());
        assertEquals(passwordNo1, testAdminInputDTO.getAdminPassword());
    }

    @Test
    public void adminInputDTOAdminLoginSetterTestPositive() {
        String loginBefore = adminInputDTO.getAdminLogin();
        assertNotNull(loginBefore);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        adminInputDTO.setAdminLogin(newLogin);
        String loginAfter = adminInputDTO.getAdminLogin();
        assertNotNull(loginAfter);

        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void adminInputDTOAdminPasswordSetterTestPositive() {
        String passwordBefore = adminInputDTO.getAdminPassword();
        assertNotNull(passwordBefore);
        String newPassword = "NewPasswordNo1";
        assertNotNull(newPassword);
        adminInputDTO.setAdminPassword(newPassword);
        String passwordAfter = adminInputDTO.getAdminPassword();
        assertNotNull(passwordAfter);

        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }
}