package pl.pas.gr3.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDTO {

    private String userLogin;
    private String userPassword;
}
