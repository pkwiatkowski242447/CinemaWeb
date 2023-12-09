package pl.pas.gr3.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminDTO {


    @JsonbProperty("id")
    @NotNull
    protected UUID adminID;

    @JsonbProperty("login")
    protected String adminLogin;

    @JsonbProperty("status-active")
    protected boolean adminStatusActive;

    @JsonbCreator
    public AdminDTO(@JsonbProperty("id") UUID adminID,
                    @JsonbProperty("login") String adminLogin,
                    @JsonbProperty("status-active") boolean adminStatusActive) {
        this.adminID = adminID;
        this.adminLogin = adminLogin;
        this.adminStatusActive = adminStatusActive;
    }
}
