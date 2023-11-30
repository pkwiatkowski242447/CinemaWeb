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
    @JsonbProperty("password")
    private String staffPassword;

    @JsonbCreator
    public StaffPasswordDTO(@JsonbProperty("id") UUID staffID,
                            @JsonbProperty("login") String staffLogin,
                            @JsonbProperty("password") String staffPassword,
                            @JsonbProperty("status-active") boolean staffStatusActive) {
        super(staffID, staffLogin, staffStatusActive);
        this.staffPassword = staffPassword;
    }
}
