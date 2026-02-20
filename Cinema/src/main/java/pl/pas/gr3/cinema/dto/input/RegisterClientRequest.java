package pl.pas.gr3.cinema.dto.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RegisterClientRequest {

    @JsonProperty("login")
    @JsonbProperty("login")
    private String login;

    @JsonProperty("password")
    @JsonbProperty("password")
    private String password;

    @JsonCreator
    @JsonbCreator
    public RegisterClientRequest(@JsonProperty("login") @JsonbProperty("login") String login,
                                 @JsonProperty("password") @JsonbProperty("password") String password) {
        this.login = login;
        this.password = password;
    }
}
