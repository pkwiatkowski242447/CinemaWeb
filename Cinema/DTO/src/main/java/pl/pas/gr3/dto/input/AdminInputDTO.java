package pl.pas.gr3.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class AdminInputDTO {

    @JsonProperty("login")
    private String adminLogin;

    @JsonProperty("password")
    private String adminPassword;

    @JsonCreator
    public AdminInputDTO(@JsonProperty("login") String adminLogin,
                         @JsonProperty("password") String adminPassword) {
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }
}
