package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.dto.users.StaffInputDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StaffInputDTOTest {

    private static String loginNo1;
    private static String passwordNo1;

    private StaffInputDTO staffInputDTO;

    @BeforeAll
    public static void init() {
        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
    }

    @BeforeEach
    public void initializeStaffInputDTO() {
        staffInputDTO = new StaffInputDTO(loginNo1, passwordNo1);
    }

    @Test
    public void createStaffInputDTONoArgsConstructorTestPositive() {
        StaffInputDTO testStaffInputDTO = new StaffInputDTO();
        assertNotNull(testStaffInputDTO);
    }

    @Test
    public void createStaffInputDTOAllArgsConstructorAndGettersTestPositive() {
        StaffInputDTO testStaffInputDTO = new StaffInputDTO(loginNo1, passwordNo1);
        assertNotNull(testStaffInputDTO);
        assertEquals(loginNo1, testStaffInputDTO.getStaffLogin());
        assertEquals(passwordNo1, testStaffInputDTO.getStaffPassword());
    }

    @Test
    public void staffInputDTOLoginSetterTestPositive() {
        String loginBefore = staffInputDTO.getStaffLogin();
        assertNotNull(loginBefore);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        staffInputDTO.setStaffLogin(newLogin);
        String loginAfter = staffInputDTO.getStaffLogin();
        assertNotNull(loginAfter);

        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void staffInputDTOPasswordSetterTestPositive() {
        String passwordBefore = staffInputDTO.getStaffPassword();
        assertNotNull(passwordBefore);
        String newPassword = "NewPasswordNo1";
        assertNotNull(newPassword);
        staffInputDTO.setStaffPassword(newPassword);
        String passwordAfter = staffInputDTO.getStaffPassword();
        assertNotNull(passwordAfter);

        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }
}
