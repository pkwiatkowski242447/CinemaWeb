package pl.pas.gr3.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ClientInputDTO {

    @JsonProperty("login")
    private String clientLogin;

    @JsonProperty("password")
    private String clientPassword;

    @JsonCreator
    public ClientInputDTO(@JsonProperty("login") String clientLogin,
                          @JsonProperty("password") String clientPassword) {
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
    }
}
