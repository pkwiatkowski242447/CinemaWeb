package pl.pas.gr3.cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;

import java.util.UUID;

@MappedSuperclass
@Getter
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = DatabaseConstants.PK_COLUMN, unique = true, nullable = false)
    private UUID id;

    @Version
    @Column(name = DatabaseConstants.VERSION_COLUMN, nullable = false)
    private Long version;

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AbstractEntity that)) return false;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}
