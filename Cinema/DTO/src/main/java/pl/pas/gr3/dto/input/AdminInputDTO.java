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
public class AdminInputDTO {

    @JsonProperty("login")
    @JsonbProperty("login")
    private String adminLogin;

    @JsonProperty("password")
    @JsonbProperty("password")
    private String adminPassword;

    @JsonCreator
    @JsonbCreator
    public AdminInputDTO(@JsonProperty("login") @JsonbProperty("login") String adminLogin,
                         @JsonProperty("password") @JsonbProperty("password") String adminPassword) {
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }
}
