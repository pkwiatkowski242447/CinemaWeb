package pl.pas.gr3.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class StaffInputDTO {

    @JsonProperty("login")
    private String staffLogin;

    @JsonProperty("password")
    private String staffPassword;

    @JsonCreator
    public StaffInputDTO(@JsonProperty("login") String staffLogin,
                         @JsonProperty("password") String staffPassword) {
        this.staffLogin = staffLogin;
        this.staffPassword = staffPassword;
    }
}
