package pl.pas.gr3.dto.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ClientInputDTO {

    @JsonProperty("login")
    @JsonbProperty("login")
    private String clientLogin;

    @JsonProperty("password")
    @JsonbProperty("password")
    private String clientPassword;

    @JsonCreator
    @JsonbCreator
    public ClientInputDTO(@JsonProperty("login") @JsonbProperty("login") String clientLogin,
                          @JsonProperty("password") @JsonbProperty("password") String clientPassword) {
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
    }
}
