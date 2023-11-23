package pl.pas.gr3.cinema.dto.json.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientJson {

    @JsonbProperty("id")
    protected UUID clientID;

    @JsonbProperty("login")
    protected String clientLogin;

    @JsonbProperty("status")
    protected boolean clientStatusActive;

    @JsonbCreator
    public ClientJson(@JsonbProperty("id") UUID clientID,
                      @JsonbProperty("login") String clientLogin,
                      @JsonbProperty("status") boolean clientStatusActive) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientStatusActive = clientStatusActive;
    }
}
