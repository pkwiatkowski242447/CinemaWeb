package pl.pas.gr3.cinema.model.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;

@Entity
@Table(name = DatabaseConstants.STAFF_TABLE)
@DiscriminatorValue(value = DatabaseConstants.STAFFS_DISCRIMINATOR)
@Getter
@NoArgsConstructor
public class Staff extends AccessLevel {}
