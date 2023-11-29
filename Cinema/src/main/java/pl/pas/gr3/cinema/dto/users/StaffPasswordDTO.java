package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffPasswordDTO extends StaffDTO {
    @JsonbProperty("staff-password")
    private String staffPassword;

    @JsonbCreator
    public StaffPasswordDTO(@JsonbProperty("staff-id") UUID staffID,
                            @JsonbProperty("staff-login") String staffLogin,
                            @JsonbProperty("staff-password") String staffPassword,
                            @JsonbProperty("staff-status-active") boolean staffStatusActive) {
        super(staffID, staffLogin, staffStatusActive);
        this.staffPassword = staffPassword;
    }
}
