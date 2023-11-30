package pl.pas.gr3.cinema.dto.users;

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
    private String login;

    @JsonbProperty("password")
    private String password;

    @JsonbCreator
    public AdminInputDTO(@JsonbProperty("login") String login,
                         @JsonbProperty("password") String password) {
        this.login = login;
        this.password = password;
    }
}
