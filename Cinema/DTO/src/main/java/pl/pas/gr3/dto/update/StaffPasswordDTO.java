package pl.pas.gr3.dto.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.StaffDTO;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffPasswordDTO extends StaffDTO {

    @JsonProperty("password")
    private String staffPassword;

    @JsonCreator
    public StaffPasswordDTO(@JsonProperty("id") UUID staffID,
                            @JsonProperty("login") String staffLogin,
                            @JsonProperty("password") String staffPassword,
                            @JsonProperty("status-active") boolean staffStatusActive) {
        super(staffID, staffLogin, staffStatusActive);
        this.staffPassword = staffPassword;
    }
}
