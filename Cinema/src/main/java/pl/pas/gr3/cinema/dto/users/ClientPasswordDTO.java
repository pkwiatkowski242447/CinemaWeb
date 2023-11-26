package pl.pas.gr3.cinema.dto.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientPasswordDTO extends ClientDTO {
    private String clientPassword;

    public ClientPasswordDTO(UUID clientID, String clientLogin, String clientPassword, boolean clientStatusActive) {
        super(clientID, clientLogin, clientStatusActive);
        this.clientPassword = clientPassword;
    }
}
