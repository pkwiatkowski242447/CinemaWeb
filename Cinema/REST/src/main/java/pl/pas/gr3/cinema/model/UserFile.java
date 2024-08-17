package pl.pas.gr3.cinema.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;

import java.util.UUID;

@Entity
@Table(name = DatabaseConstants.USER_FILE_TABLE)
@Getter @Setter
@NoArgsConstructor
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = DatabaseConstants.PK_COLUMN, unique = true, nullable = false)
    private UUID id;

    @Column(name = DatabaseConstants.USER_FILE_PATH_COLUMN, nullable = false, unique = true)
    private String path;

    @Column(name = DatabaseConstants.USER_ORIGINAL_FILE_NAME_COLUMN, nullable = false)
    private String originalFileName;

    @Column(name = DatabaseConstants.USER_FILE_FORMAT_COLUMN, nullable = false)
    private String format;

    // Constructors

    public UserFile(String path,
                    String format,
                    String originalFileName) {
        this.path = path;
        this.format = format;
        this.originalFileName = originalFileName;
    }
}
