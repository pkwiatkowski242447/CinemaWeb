package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientDTO {

    @JsonbProperty("client-id")
    protected UUID clientID;

    @JsonbProperty("client-login")
    protected String clientLogin;

    @JsonbProperty("client-status-active")
    protected boolean clientStatusActive;

    @JsonbCreator
    public ClientDTO(@JsonbProperty("client-id") UUID clientID,
                     @JsonbProperty("client-login") String clientLogin,
                     @JsonbProperty("client-status-active") boolean clientStatusActive) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientStatusActive = clientStatusActive;
    }
}
