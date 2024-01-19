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

    @JsonbProperty("id")
    protected UUID staffID;

    @JsonbProperty("login")
    protected String staffLogin;

    @JsonbProperty("status-active")
    protected boolean staffStatusActive;

    @JsonbCreator
    public StaffDTO(@JsonbProperty("id") UUID staffID,
                    @JsonbProperty("login") String staffLogin,
                    @JsonbProperty("status-active") boolean staffStatusActive) {
        this.staffID = staffID;
        this.staffLogin = staffLogin;
        this.staffStatusActive = staffStatusActive;
    }
}
