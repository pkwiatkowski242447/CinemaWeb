package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientPasswordDTO extends ClientDTO {

    @JsonbProperty("client-password")
    private String clientPassword;

    @JsonbCreator
    public ClientPasswordDTO(@JsonbProperty("client-id") UUID clientID,
                             @JsonbProperty("client-login") String clientLogin,
                             @JsonbProperty("client-password") String clientPassword,
                             @JsonbProperty("client-status-active") boolean clientStatusActive) {
        super(clientID, clientLogin, clientStatusActive);
        this.clientPassword = clientPassword;
    }
}
