package pl.pas.gr3.cinema.dto.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.dto.output.StaffDTO;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffPasswordDTO extends StaffDTO {

    @JsonProperty("password")
    @JsonbProperty("password")
    private String staffPassword;

    @JsonCreator
    @JsonbCreator
    public StaffPasswordDTO(@JsonProperty("id") @JsonbProperty("id") UUID staffID,
                            @JsonProperty("login") @JsonbProperty("login") String staffLogin,
                            @JsonProperty("password") @JsonbProperty("password") String staffPassword,
                            @JsonProperty("status-active") @JsonbProperty("status-active") boolean staffStatusActive) {
        super(staffID, staffLogin, staffStatusActive);
        this.staffPassword = staffPassword;
    }
}
