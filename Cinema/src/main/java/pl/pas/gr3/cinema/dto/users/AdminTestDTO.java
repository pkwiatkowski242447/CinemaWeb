package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class AdminTestDTO {

    @JsonbProperty("admin-login")
    private String adminLogin;

    @JsonbProperty("admin-password")
    private String adminPassword;

    @JsonbCreator
    public AdminTestDTO(@JsonbProperty("admin-login") String adminLogin,
                        @JsonbProperty("admin-password") String adminPassword) {
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }
}
