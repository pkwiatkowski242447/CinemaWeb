package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffDTO {

    @JsonbProperty("staff-id")
    protected UUID staffID;

    @JsonbProperty("staff-login")
    protected String staffLogin;

    @JsonbProperty("staff-status-active")
    protected boolean staffStatusActive;

    @JsonbCreator
    public StaffDTO(@JsonbProperty("staff-id") UUID staffID,
                    @JsonbProperty("staff-login") String staffLogin,
                    @JsonbProperty("staff-status-active") boolean staffStatusActive) {
        this.staffID = staffID;
        this.staffLogin = staffLogin;
        this.staffStatusActive = staffStatusActive;
    }
}
