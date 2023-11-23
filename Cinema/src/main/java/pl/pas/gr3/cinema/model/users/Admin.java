package pl.pas.gr3.cinema.model.users;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends Client {

    public Admin(UUID clientID, String clientLogin, String clientPassword) {
        super(clientID, clientLogin, clientPassword);
    }

    public Admin(UUID clientID, String clientLogin, String clientPassword, boolean clientStatusActive) {
        super(clientID, clientLogin, clientPassword, clientStatusActive);
    }
}
