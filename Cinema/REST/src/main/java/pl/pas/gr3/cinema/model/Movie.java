package pl.pas.gr3.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;
import pl.pas.gr3.cinema.utils.messages.MovieConstants;

@Entity
@Table(name = DatabaseConstants.MOVIES_TABLE)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie extends AbstractEntity {

    @NotBlank(message = MovieConstants.MOVIE_TITLE_BLANK)
    @Size(min = MovieConstants.MOVIE_TITLE_MIN_LENGTH, message = MovieConstants.MOVIE_TITLE_TOO_SHORT)
    @Size(max = MovieConstants.MOVIE_TITLE_MAX_LENGTH, message = MovieConstants.MOVIE_TITLE_TOO_LONG)
    @Column(name = DatabaseConstants.MOVIES_TITLE_COLUMN, nullable = false, length = 128)
    private String title;

    @NotBlank(message = MovieConstants.MOVIE_DESCRIPTION_BLANK)
    @Size(min = MovieConstants.MOVIE_DESCRIPTION_MIN_LENGTH, message = MovieConstants.MOVIE_DESCRIPTION_TOO_SHORT)
    @Size(max = MovieConstants.MOVIE_DESCRIPTION_MAX_LENGTH, message = MovieConstants.MOVIE_DESCRIPTION_TOO_LONG)
    @Column(name = DatabaseConstants.MOVIES_DESCRIPTION_COLUMN, nullable = false, length = 512)
    private String description;

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return new EqualsBuilder()
                .append(title, movie.title)
                .append(description, movie.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(title)
                .append(description)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(super.toString())
                .toString();
    }
}