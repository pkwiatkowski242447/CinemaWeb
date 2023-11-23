package pl.pas.gr3.cinema.dto.json.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffJson {

    @JsonbProperty("id")
    protected UUID staffID;

    @JsonbProperty("login")
    protected String staffLogin;

    @JsonbProperty("status")
    protected boolean staffStatusActive;

    @JsonbCreator
    public StaffJson(@JsonbProperty("id") UUID staffID,
                     @JsonbProperty("login") String staffLogin,
                     @JsonbProperty("status") boolean staffStatusActive) {
        this.staffID = staffID;
        this.staffLogin = staffLogin;
        this.staffStatusActive = staffStatusActive;
    }
}
