package pl.pas.gr3.cinema.dto.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StaffInputDTO {

    @JsonbProperty("login")
    private String staffLogin;

    @JsonbProperty("password")
    private String staffPassword;

    @JsonbCreator
    public StaffInputDTO(@JsonbProperty("login") String staffLogin,
                          @JsonbProperty("password") String staffPassword) {
        this.staffLogin = staffLogin;
        this.staffPassword = staffPassword;
    }
}
