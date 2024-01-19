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

    @JsonbProperty("id")
    protected UUID clientID;

    @JsonbProperty("login")
    protected String clientLogin;

    @JsonbProperty("status-active")
    protected boolean clientStatusActive;

    @JsonbCreator
    public ClientDTO(@JsonbProperty("id") UUID clientID,
                     @JsonbProperty("login") String clientLogin,
                     @JsonbProperty("status-active") boolean clientStatusActive) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientStatusActive = clientStatusActive;
    }
}