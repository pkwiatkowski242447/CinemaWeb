package pl.pas.gr3.cinema.dto.json.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminJsonPassword extends AdminJson {

    @JsonbProperty("password")
    private String adminPassword;

    @JsonbCreator
    public AdminJsonPassword(@JsonbProperty("id") UUID adminID,
                             @JsonbProperty("login") String adminLogin,
                             @JsonbProperty("status") boolean adminStatusActive,
                             @JsonbProperty("password") String adminPassword) {
        super(adminID, adminLogin, adminStatusActive);
        this.adminPassword = adminPassword;
    }
}
