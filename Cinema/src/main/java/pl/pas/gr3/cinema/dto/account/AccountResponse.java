package pl.pas.gr3.cinema.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse extends AccountSignatureResponse {

    private UUID id;
    private String login;
    private boolean active;
}
