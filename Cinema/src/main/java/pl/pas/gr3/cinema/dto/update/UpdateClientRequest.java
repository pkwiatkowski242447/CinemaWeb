package pl.pas.gr3.cinema.dto.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.dto.output.ClientResponse;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class UpdateClientRequest extends ClientResponse {

    @JsonProperty("password")
    @JsonbProperty("password")
    private String password;

    @JsonCreator
    @JsonbCreator
    public UpdateClientRequest(@JsonProperty("id") @JsonbProperty("id") UUID id,
                               @JsonProperty("login") @JsonbProperty("login") String login,
                               @JsonProperty("password") @JsonbProperty("password") String password,
                               @JsonProperty("status-active") @JsonbProperty("status-active") boolean active) {
        super(id, login, active);
        this.password = password;
    }
}
