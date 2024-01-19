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
    public ClientPasswordDTO(@JsonbProperty("id") UUID clientID,
                             @JsonbProperty("login") String clientLogin,
                             @JsonbProperty("password") String clientPassword,
                             @JsonbProperty("status-active") boolean clientStatusActive) {
        super(clientID, clientLogin, clientStatusActive);
        this.clientPassword = clientPassword;
    }
}
