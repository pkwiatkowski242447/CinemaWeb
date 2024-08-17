package pl.pas.gr3.dto.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.ClientDTO;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientPasswordDTO extends ClientDTO {

    @JsonProperty("password")
    private String clientPassword;

    @JsonCreator
    public ClientPasswordDTO(@JsonProperty("id") UUID clientID,
                             @JsonProperty("login") String clientLogin,
                             @JsonProperty("password") String clientPassword,
                             @JsonProperty("status-active") boolean clientStatusActive) {
        super(clientID, clientLogin, clientStatusActive);
        this.clientPassword = clientPassword;
    }
}
