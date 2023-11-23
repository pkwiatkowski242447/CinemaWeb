package pl.pas.gr3.cinema.dto.json.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientJsonPassword extends ClientJson {

    @JsonbProperty("password")
    private String clientPassword;

    @JsonbCreator
    public ClientJsonPassword(@JsonbProperty("id") UUID clientID,
                              @JsonbProperty("login") String clientLogin,
                              @JsonbProperty("status") boolean clientStatusActive,
                              @JsonbProperty("password") String clientPassword) {
        super(clientID, clientLogin, clientStatusActive);
        this.clientPassword = clientPassword;
    }
}
