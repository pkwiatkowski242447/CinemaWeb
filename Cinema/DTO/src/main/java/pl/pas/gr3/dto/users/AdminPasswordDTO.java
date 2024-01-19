package pl.pas.gr3.dto.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminPasswordDTO extends AdminDTO {

    @JsonProperty("password")
    @JsonbProperty("password")
    private String adminPassword;

    @JsonCreator
    @JsonbCreator
    public AdminPasswordDTO(@JsonProperty("id") @JsonbProperty("id") UUID adminID,
                            @JsonProperty("login") @JsonbProperty("login") String adminLogin,
                            @JsonProperty("password") @JsonbProperty("password") String adminPassword,
                            @JsonProperty("status-active") @JsonbProperty("status-active") boolean adminStatusActive) {
        super(adminID, adminLogin, adminStatusActive);
        this.adminPassword = adminPassword;
    }
}
