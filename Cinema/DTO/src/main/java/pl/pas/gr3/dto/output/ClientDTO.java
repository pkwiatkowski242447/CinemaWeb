package pl.pas.gr3.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class ClientDTO {

    @JsonProperty("id")
    protected UUID clientID;

    @JsonProperty("login")
    protected String clientLogin;

    @JsonProperty("status-active")
    protected boolean clientStatusActive;

    @JsonCreator
    public ClientDTO(@JsonProperty("id") UUID clientID,
                     @JsonProperty("login") String clientLogin,
                     @JsonProperty("status-active") boolean clientStatusActive) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientStatusActive = clientStatusActive;
    }
}