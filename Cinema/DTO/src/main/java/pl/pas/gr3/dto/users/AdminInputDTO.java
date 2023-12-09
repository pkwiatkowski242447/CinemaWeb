package pl.pas.gr3.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminInputDTO {

    @JsonbProperty("login")
    private String adminLogin;

    @JsonbProperty("password")
    private String adminPassword;

    @JsonbCreator
    public AdminInputDTO(@JsonbProperty("login") String adminLogin,
                         @JsonbProperty("password") String adminPassword) {
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }
}
