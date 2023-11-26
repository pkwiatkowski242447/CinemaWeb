package pl.pas.gr3.cinema.dto.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffPasswordDTO extends StaffDTO {
    private String staffPassword;

    public StaffPasswordDTO(UUID staffID, String staffLogin, String staffPassword, boolean staffStatusActive) {
        super(staffID, staffLogin, staffStatusActive);
        this.staffPassword = staffPassword;
    }
}
