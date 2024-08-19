package pl.pas.gr3.cinema.model.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;

@Entity
@Table(name = DatabaseConstants.CLIENTS_TABLE)
@DiscriminatorValue(value = DatabaseConstants.CLIENTS_DISCRIMINATOR)
@Getter
@NoArgsConstructor
public class Client extends AccessLevel {}
