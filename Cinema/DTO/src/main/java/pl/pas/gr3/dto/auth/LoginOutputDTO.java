package pl.pas.gr3.dto.auth;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOutputDTO {

    String accessToken;
}
