package pl.pas.gr3.dto.auth;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO extends UserOutputDTO {

    private String userPassword;
}
