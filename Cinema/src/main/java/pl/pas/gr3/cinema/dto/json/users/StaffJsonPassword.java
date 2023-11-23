package pl.pas.gr3.cinema.dto.json.users;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class StaffJsonPassword extends StaffJson {

    @JsonbProperty("password")
    private String staffPassword;

    @JsonbCreator
    public StaffJsonPassword(@JsonbProperty("id") UUID staffID,
                             @JsonbProperty("login") String staffLogin,
                             @JsonbProperty("status") boolean staffStatusActive,
                             @JsonbProperty("password") String staffPassword) {
        super(staffID, staffLogin, staffStatusActive);
        this.staffPassword = staffPassword;
    }
}
