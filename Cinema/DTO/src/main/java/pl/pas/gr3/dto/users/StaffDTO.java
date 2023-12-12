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
public class StaffDTO {

    @JsonProperty("id")
    @JsonbProperty("id")
    protected UUID staffID;

    @JsonProperty("login")
    @JsonbProperty("login")
    protected String staffLogin;

    @JsonProperty("status-active")
    @JsonbProperty("status-active")
    protected boolean staffStatusActive;

    @JsonCreator
    @JsonbCreator
    public StaffDTO(@JsonProperty("id") @JsonbProperty("id") UUID staffID,
                    @JsonProperty("login") @JsonbProperty("login") String staffLogin,
                    @JsonProperty("status-active") @JsonbProperty("status-active") boolean staffStatusActive) {
        this.staffID = staffID;
        this.staffLogin = staffLogin;
        this.staffStatusActive = staffStatusActive;
    }
}
