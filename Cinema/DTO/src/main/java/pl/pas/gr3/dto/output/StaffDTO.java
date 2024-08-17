package pl.pas.gr3.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffDTO {

    @JsonProperty("id")
    protected UUID staffID;

    @JsonProperty("login")
    protected String staffLogin;

    @JsonProperty("status-active")
    protected boolean staffStatusActive;

    @JsonCreator
    public StaffDTO(@JsonProperty("id") UUID staffID,
                    @JsonProperty("login") String staffLogin,
                    @JsonProperty("status-active") boolean staffStatusActive) {
        this.staffID = staffID;
        this.staffLogin = staffLogin;
        this.staffStatusActive = staffStatusActive;
    }
}