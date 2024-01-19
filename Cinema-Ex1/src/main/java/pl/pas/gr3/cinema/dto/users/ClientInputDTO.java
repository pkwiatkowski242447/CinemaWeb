package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientInputDTO {

    @JsonbProperty("login")
    private String clientLogin;

    @JsonbProperty("password")
    private String clientPassword;

    @JsonbCreator
    public ClientInputDTO(@JsonbProperty("login") String clientLogin,
                          @JsonbProperty("password") String clientPassword) {
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
    }
}
