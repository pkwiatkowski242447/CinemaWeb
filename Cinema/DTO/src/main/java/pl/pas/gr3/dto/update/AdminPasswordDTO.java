package pl.pas.gr3.dto.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.AdminDTO;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminPasswordDTO extends AdminDTO {

    @JsonProperty("password")
    private String adminPassword;

    @JsonCreator
    public AdminPasswordDTO(@JsonProperty("id") UUID adminID,
                            @JsonProperty("login") String adminLogin,
                            @JsonProperty("password") String adminPassword,
                            @JsonProperty("status-active") boolean adminStatusActive) {
        super(adminID, adminLogin, adminStatusActive);
        this.adminPassword = adminPassword;
    }
}
