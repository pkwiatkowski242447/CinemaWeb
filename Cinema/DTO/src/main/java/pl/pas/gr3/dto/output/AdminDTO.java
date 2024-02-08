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
public class AdminDTO {

    @JsonProperty("id")
    @JsonbProperty("id")
    protected UUID adminID;

    @JsonProperty("login")
    @JsonbProperty("login")
    protected String adminLogin;

    @JsonProperty("status-active")
    @JsonbProperty("status-active")
    protected boolean adminStatusActive;

    @JsonCreator
    @JsonbCreator
    public AdminDTO(@JsonProperty("id") @JsonbProperty("id") UUID adminID,
                    @JsonProperty("login") @JsonbProperty("login") String adminLogin,
                    @JsonProperty("status-active") @JsonbProperty("status-active") boolean adminStatusActive) {
        this.adminID = adminID;
        this.adminLogin = adminLogin;
        this.adminStatusActive = adminStatusActive;
    }
}
