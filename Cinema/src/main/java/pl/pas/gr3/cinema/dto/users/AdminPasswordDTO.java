package pl.pas.gr3.cinema.dto.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminPasswordDTO extends AdminDTO {
    private String adminPassword;

    public AdminPasswordDTO(UUID adminID, String adminLogin, String adminPassword, boolean adminStatusActive) {
        super(adminID, adminLogin, adminStatusActive);
        this.adminPassword = adminPassword;
    }
}
