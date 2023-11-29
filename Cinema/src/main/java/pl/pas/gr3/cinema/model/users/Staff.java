package pl.pas.gr3.cinema.model.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Staff extends Client {

    public Staff(UUID clientID, String clientLogin, String clientPassword, boolean clientStatusActive) {
        super(clientID, clientLogin, clientPassword, clientStatusActive);
    }

    public Staff(UUID clientID, String clientLogin, String clientPassword) {
        super(clientID, clientLogin, clientPassword);
    }


}
