package pl.pas.gr3.dto.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientPasswordDTO extends ClientDTO {

    @JsonProperty("password")
    @JsonbProperty("password")
    private String clientPassword;

    @JsonCreator
    @JsonbCreator
    public ClientPasswordDTO(@JsonProperty("id") @JsonbProperty("id") UUID clientID,
                             @JsonProperty("login") @JsonbProperty("login") String clientLogin,
                             @JsonProperty("password") @JsonbProperty("password") String clientPassword,
                             @JsonProperty("status-active") @JsonbProperty("status-active") boolean clientStatusActive) {
        super(clientID, clientLogin, clientStatusActive);
        this.clientPassword = clientPassword;
    }
}
