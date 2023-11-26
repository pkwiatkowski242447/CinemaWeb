package pl.pas.gr3.cinema.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO {

    protected UUID staffID;
    protected String staffLogin;
    protected boolean staffStatusActive;
}
