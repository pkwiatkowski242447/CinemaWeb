package pl.pas.gr3.cinema.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    protected UUID adminID;
    protected String adminLogin;
    protected boolean adminStatusActive;
}
