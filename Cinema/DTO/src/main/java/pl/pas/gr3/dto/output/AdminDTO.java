package pl.pas.gr3.dto.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class AdminDTO {

    @JsonProperty("id")
    protected UUID adminID;

    @JsonProperty("login")
    protected String adminLogin;

    @JsonProperty("status-active")
    protected boolean adminStatusActive;

    @JsonCreator
    public AdminDTO(@JsonProperty("id") UUID adminID,
                    @JsonProperty("login") String adminLogin,
                    @JsonProperty("status-active") boolean adminStatusActive) {
        this.adminID = adminID;
        this.adminLogin = adminLogin;
        this.adminStatusActive = adminStatusActive;
    }
}
