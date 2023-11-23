package pl.pas.gr3.cinema.dto.json.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminJson {

    @JsonbProperty("id")
    protected UUID adminID;

    @JsonbProperty("login")
    protected String adminLogin;

    @JsonbProperty("status")
    protected boolean adminStatusActive;

    @JsonbCreator
    public AdminJson(@JsonbProperty("id") UUID adminID,
                     @JsonbProperty("login") String adminLogin,
                     @JsonbProperty("status") boolean adminStatusActive) {
        this.adminID = adminID;
        this.adminLogin = adminLogin;
        this.adminStatusActive = adminStatusActive;
    }
}
