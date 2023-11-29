package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminDTO {

    @JsonbProperty("admin-id")
    protected UUID adminID;

    @JsonbProperty("admin-login")
    protected String adminLogin;

    @JsonbProperty("admin-status-active")
    protected boolean adminStatusActive;

    @JsonbCreator
    public AdminDTO(@JsonbProperty("admin-id") UUID adminID,
                    @JsonbProperty("admin-login") String adminLogin,
                    @JsonbProperty("admin-status-active") boolean adminStatusActive) {
        this.adminID = adminID;
        this.adminLogin = adminLogin;
        this.adminStatusActive = adminStatusActive;
    }
}
