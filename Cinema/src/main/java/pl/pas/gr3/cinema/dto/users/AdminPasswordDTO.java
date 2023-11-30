package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminPasswordDTO extends AdminDTO {

    @JsonbProperty("password")
    private String adminPassword;

    @JsonbCreator
    public AdminPasswordDTO(@JsonbProperty("id") UUID adminID,
                            @JsonbProperty("login") String adminLogin,
                            @JsonbProperty("password") String adminPassword,
                            @JsonbProperty("status-active") boolean adminStatusActive) {
        super(adminID, adminLogin, adminStatusActive);
        this.adminPassword = adminPassword;
    }
}
