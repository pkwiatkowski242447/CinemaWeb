package pl.pas.gr3.dto.output;

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
public class ClientDTO {

    @JsonProperty("id")
    @JsonbProperty("id")
    protected UUID clientID;

    @JsonProperty("login")
    @JsonbProperty("login")
    protected String clientLogin;

    @JsonProperty("status-active")
    @JsonbProperty("status-active")
    protected boolean clientStatusActive;

    @JsonCreator
    @JsonbCreator
    public ClientDTO(@JsonProperty("id") @JsonbProperty("id") UUID clientID,
                     @JsonProperty("login") @JsonbProperty("login") String clientLogin,
                     @JsonProperty("status-active") @JsonbProperty("status-active") boolean clientStatusActive) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientStatusActive = clientStatusActive;
    }
}