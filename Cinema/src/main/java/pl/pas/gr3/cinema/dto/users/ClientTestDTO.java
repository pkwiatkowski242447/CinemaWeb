package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ClientTestDTO {
    @JsonbProperty("client-login")
    private String clientLogin;

    @JsonbProperty("client-password")
    private String clientPassword;

    @JsonbCreator
    public ClientTestDTO(@JsonbProperty("client-login") String clientLogin,
                         @JsonbProperty("client-password") String clientPassword) {
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
    }
}
