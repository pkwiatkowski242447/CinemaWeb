package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.dto.update.ClientPasswordDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientPasswordDTOTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static String passwordNo1;
    private static boolean statusActiveNo1;

    private ClientPasswordDTO clientPasswordDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "SomeLoginNo1";
        passwordNo1 = "SomePasswordNo1";
        statusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeClientPasswordDTO() {
        clientPasswordDTO = new ClientPasswordDTO(uuidNo1, loginNo1, passwordNo1, statusActiveNo1);
    }

    @Test
    public void clientPasswordDTONoArgsConstructorTestPositive() {
        ClientPasswordDTO testClientPasswordDTO = new ClientPasswordDTO();
        assertNotNull(testClientPasswordDTO);
    }

    @Test
    public void clientPasswordDTOAllArgsConstructorAndGettersTestPositive() {
        ClientPasswordDTO testClientPasswordDTO = new ClientPasswordDTO(uuidNo1, loginNo1, passwordNo1, statusActiveNo1);
        assertNotNull(testClientPasswordDTO);
        assertEquals(uuidNo1, clientPasswordDTO.getClientID());
        assertEquals(loginNo1, clientPasswordDTO.getClientLogin());
        assertEquals(passwordNo1, clientPasswordDTO.getClientPassword());
        assertEquals(statusActiveNo1, clientPasswordDTO.isClientStatusActive());
    }

    @Test
    public void clientPasswordDTOIDSetterTestPositive() {
        UUID oldUUID = clientPasswordDTO.getClientID();
        assertNotNull(oldUUID);
        UUID newUUID = UUID.randomUUID();
        assertNotNull(newUUID);
        assertNotEquals(newUUID, oldUUID);
        clientPasswordDTO.setClientID(newUUID);
        assertNotNull(clientPasswordDTO.getClientID());
        assertEquals(newUUID, clientPasswordDTO.getClientID());
        assertNotEquals(oldUUID, clientPasswordDTO.getClientID());
    }

    @Test
    public void clientPasswordDTOLoginSetterTestPositive() {
        String oldLogin = clientPasswordDTO.getClientLogin();
        assertNotNull(oldLogin);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        assertNotEquals(oldLogin, newLogin);
        clientPasswordDTO.setClientLogin(newLogin);
        assertNotNull(clientPasswordDTO.getClientLogin());
        assertEquals(newLogin, clientPasswordDTO.getClientLogin());
        assertNotEquals(oldLogin, clientPasswordDTO.getClientLogin());
    }

    @Test
    public void clientPasswordDTOPasswordSetterTestPositive() {
        String oldPassword = clientPasswordDTO.getClientPassword();
        assertNotNull(oldPassword);
        String newPassword = "NewPasswordNo1";
        assertNotNull(newPassword);
        assertNotEquals(oldPassword, newPassword);
        clientPasswordDTO.setClientPassword(newPassword);
        assertNotNull(clientPasswordDTO.getClientPassword());
        assertEquals(newPassword, clientPasswordDTO.getClientPassword());
        assertNotEquals(oldPassword, clientPasswordDTO.getClientPassword());
    }

    @Test
    public void clientPasswordDTOStatusActiveSetterTestPositive() {
        boolean oldStatusActive = clientPasswordDTO.isClientStatusActive();
        boolean newStatusActive = !oldStatusActive;
        clientPasswordDTO.setClientStatusActive(newStatusActive);
        assertEquals(newStatusActive, clientPasswordDTO.isClientStatusActive());
        assertNotEquals(oldStatusActive, clientPasswordDTO.isClientStatusActive());
    }
}