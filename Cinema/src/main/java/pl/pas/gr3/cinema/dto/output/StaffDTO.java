package pl.pas.gr3.cinema.dto.output;

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
public class StaffDTO {

    @JsonProperty("id")
    @JsonbProperty("id")
    protected UUID id;

    @JsonProperty("login")
    @JsonbProperty("login")
    protected String login;

    @JsonProperty("status-active")
    @JsonbProperty("status-active")
    protected boolean active;

    @JsonCreator
    @JsonbCreator
    public StaffDTO(@JsonProperty("id") @JsonbProperty("id") UUID id,
                    @JsonProperty("login") @JsonbProperty("login") String login,
                    @JsonProperty("status-active") @JsonbProperty("status-active") boolean active) {
        this.id = id;
        this.login = login;
        this.active = active;
    }
}