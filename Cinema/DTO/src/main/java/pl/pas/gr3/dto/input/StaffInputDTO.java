package pl.pas.gr3.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class StaffInputDTO {

    @JsonProperty("login")
    @JsonbProperty("login")
    private String staffLogin;

    @JsonProperty("password")
    @JsonbProperty("password")
    private String staffPassword;

    @JsonCreator
    @JsonbCreator
    public StaffInputDTO(@JsonProperty("login") @JsonbProperty("login") String staffLogin,
                         @JsonProperty("password") @JsonbProperty("password") String staffPassword) {
        this.staffLogin = staffLogin;
        this.staffPassword = staffPassword;
    }
}
