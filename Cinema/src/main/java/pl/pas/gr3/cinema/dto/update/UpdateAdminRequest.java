package pl.pas.gr3.cinema.dto.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.dto.output.AdminResponse;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class UpdateAdminRequest extends AdminResponse {

    @JsonProperty("password")
    @JsonbProperty("password")
    private String password;

    @JsonCreator
    @JsonbCreator
    public UpdateAdminRequest(@JsonProperty("id") @JsonbProperty("id") UUID id,
                              @JsonProperty("login") @JsonbProperty("login") String login,
                              @JsonProperty("password") @JsonbProperty("password") String password,
                              @JsonProperty("status-active") @JsonbProperty("status-active") boolean active) {
        super(id, login, active);
        this.password = password;
    }
}
