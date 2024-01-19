package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.dto.users.ClientInputDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ClientInputDTOTest {

    private static String loginNo1;
    private static String passwordNo1;

    private ClientInputDTO clientInputDTO;

    @BeforeAll
    public static void init() {
        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
    }

    @BeforeEach
    public void initializeClientInputDTO() {
        clientInputDTO = new ClientInputDTO(loginNo1, passwordNo1);
    }

    @Test
    public void createClientInputDTONoArgsConstructorTestPositive() {
        ClientInputDTO testClientInputDTO = new ClientInputDTO();
        assertNotNull(testClientInputDTO);
    }

    @Test
    public void createClientInputDTOAllArgsConstructorAndGettersTestPositive() {
        ClientInputDTO testClientInputDTO = new ClientInputDTO(loginNo1, passwordNo1);
        assertNotNull(testClientInputDTO);
        assertEquals(loginNo1, testClientInputDTO.getClientLogin());
        assertEquals(passwordNo1, testClientInputDTO.getClientPassword());
    }

    @Test
    public void clientInputDTOLoginSetterTestPositive() {
        String loginBefore = clientInputDTO.getClientLogin();
        assertNotNull(loginBefore);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        clientInputDTO.setClientLogin(newLogin);
        String loginAfter = clientInputDTO.getClientLogin();
        assertNotNull(loginAfter);

        assertEquals(newLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    public void clientInputDTOPasswordSetterTestPositive() {
        String passwordBefore = clientInputDTO.getClientPassword();
        assertNotNull(passwordBefore);
        String newPassword = "NewPasswordNo1";
        assertNotNull(newPassword);
        clientInputDTO.setClientPassword(newPassword);
        String passwordAfter = clientInputDTO.getClientPassword();
        assertNotNull(passwordAfter);

        assertEquals(newPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }
}