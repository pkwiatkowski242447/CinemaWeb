package dto.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.dto.users.ClientDTO;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ClientDTOTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static boolean statusActiveNo1;

    private ClientDTO clientDTO;

    @BeforeAll
    public static void init() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "SomeLoginNo1";
        statusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeClientDTO() {
        clientDTO = new ClientDTO(uuidNo1, loginNo1, statusActiveNo1);
    }

    @Test
    public void clientDTONoArgsConstructorTestPositive() {
        ClientDTO testClientDTO = new ClientDTO();
        assertNotNull(testClientDTO);
    }

    @Test
    public void clientDTOAllArgsConstructorAndGettersTestPositive() {
        ClientDTO testClientDTO = new ClientDTO(uuidNo1, loginNo1, statusActiveNo1);
        assertNotNull(testClientDTO);
        assertEquals(uuidNo1, testClientDTO.getClientID());
        assertEquals(loginNo1, testClientDTO.getClientLogin());
        assertEquals(statusActiveNo1, testClientDTO.isClientStatusActive());
    }

    @Test
    public void clientDTOIDSetterTestPositive() {
        UUID oldUUID = clientDTO.getClientID();
        assertNotNull(oldUUID);
        UUID newUUID = UUID.randomUUID();
        assertNotNull(newUUID);
        assertNotEquals(newUUID, oldUUID);
        clientDTO.setClientID(newUUID);
        assertNotNull(clientDTO.getClientID());
        assertEquals(newUUID, clientDTO.getClientID());
        assertNotEquals(oldUUID, clientDTO.getClientID());
    }

    @Test
    public void clientDTOLoginSetterTestPositive() {
        String oldLogin = clientDTO.getClientLogin();
        assertNotNull(oldLogin);
        String newLogin = "NewLoginNo1";
        assertNotNull(newLogin);
        assertNotEquals(oldLogin, newLogin);
        clientDTO.setClientLogin(newLogin);
        assertNotNull(clientDTO.getClientLogin());
        assertEquals(newLogin, clientDTO.getClientLogin());
        assertNotEquals(oldLogin, clientDTO.getClientLogin());
    }

    @Test
    public void clientDTOStatusActiveSetterTestPositive() {
        boolean oldStatusActive = clientDTO.isClientStatusActive();
        boolean newStatusActive = !oldStatusActive;
        clientDTO.setClientStatusActive(newStatusActive);
        assertEquals(newStatusActive, clientDTO.isClientStatusActive());
        assertNotEquals(oldStatusActive, clientDTO.isClientStatusActive());
    }
}