package pl.pas.gr3.dto.auth;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOutputDTO {

    private UUID userID;
    private String userLogin;
    private boolean userStatusActive;
}
