package pl.pas.gr3.cinema.model.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.pas.gr3.cinema.model.AbstractEntity;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;

@Entity
@Table(
        name = DatabaseConstants.ACCESS_LEVELS_TABLE,
        indexes = {
                @Index(name = DatabaseConstants.ACCESS_LEVELS_ACCOUNT_ID_IDX,
                        columnList = DatabaseConstants.ACCESS_LEVELS_ACCOUNT_ID_COLUMN)
        }
)
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = DatabaseConstants.ACCESS_LEVELS_DISCRIMINATOR)
public abstract class AccessLevel extends AbstractEntity {

    @ManyToOne
    @JoinColumn(
            name = DatabaseConstants.ACCESS_LEVELS_ACCOUNT_ID_COLUMN,
            referencedColumnName = DatabaseConstants.PK_COLUMN,
            foreignKey = @ForeignKey(name = DatabaseConstants.ACCESS_LEVELS_ACCOUNT_ID_FK)
    )
    private Account account;
}
