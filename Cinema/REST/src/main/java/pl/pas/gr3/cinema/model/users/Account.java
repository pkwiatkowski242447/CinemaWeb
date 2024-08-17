package pl.pas.gr3.cinema.model.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.pas.gr3.cinema.exceptions.access_level.AccessLevelGrantException;
import pl.pas.gr3.cinema.exceptions.access_level.AccessLevelRevokeException;
import pl.pas.gr3.cinema.model.AbstractEntity;
import pl.pas.gr3.cinema.model.UserFile;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;
import pl.pas.gr3.cinema.utils.messages.AccountConstants;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = DatabaseConstants.ACCOUNTS_TABLE,
        indexes = {
                @Index(name = DatabaseConstants.ACCOUNTS_AVATAR_ID_IDX,
                        columnList = DatabaseConstants.ACCOUNTS_AVATAR_ID)
        }
)
@SecondaryTables(
        value = {
                @SecondaryTable(
                        name = DatabaseConstants.PERSONAL_DATA_TABLE,
                        pkJoinColumns = @PrimaryKeyJoinColumn(
                                name = DatabaseConstants.PERSONAL_DATA_ACCOUNT_ID,
                                referencedColumnName = DatabaseConstants.PK_COLUMN
                        ),
                        foreignKey = @ForeignKey(name = DatabaseConstants.PERSONAL_DATA_ACCOUNT_ID_FK),
                        indexes = {
                                @Index(name = DatabaseConstants.PERSONAL_DATA_ACCOUNT_ID_IDX,
                                        columnList = DatabaseConstants.PERSONAL_DATA_ACCOUNT_ID)
                        }
                ),
                @SecondaryTable(
                        name = DatabaseConstants.ADDRESS_DATA_TABLE,
                        pkJoinColumns = @PrimaryKeyJoinColumn(
                                name = DatabaseConstants.ADDRESS_DATA_ACCOUNT_ID,
                                referencedColumnName = DatabaseConstants.PK_COLUMN
                        ),
                        foreignKey = @ForeignKey(name = DatabaseConstants.ADDRESS_DATA_ACCOUNT_ID_FK),
                        indexes = {
                                @Index(name = DatabaseConstants.ADDRESS_DATA_ACCOUNT_ID_IDX,
                                        columnList = DatabaseConstants.ADDRESS_DATA_ACCOUNT_ID)
                        }
                )
        }
)
@Getter @Setter
@NoArgsConstructor
public class Account extends AbstractEntity {

    @NotNull(message = AccountConstants.ACCOUNT_LOGIN_BLANK)
    @Size(min = AccountConstants.ACCOUNT_LOGIN_MIN_LENGTH, message = AccountConstants.ACCOUNT_LOGIN_TOO_SHORT)
    @Size(max = AccountConstants.ACCOUNT_LOGIN_MAX_LENGTH, message = AccountConstants.ACCOUNT_LOGIN_TOO_LONG)
    @Column(name = DatabaseConstants.ACCOUNTS_LOGIN_COLUMN, unique = true, nullable = false, length = 64)
    private String login;

    @NotBlank(message = AccountConstants.ACCOUNT_PASSWORD_BLANK)
    @Size(min = AccountConstants.ACCOUNT_PASSWORD_LENGTH, message = AccountConstants.ACCOUNT_PASSWORD_TOO_SHORT)
    @Size(max = AccountConstants.ACCOUNT_PASSWORD_LENGTH, message = AccountConstants.ACCOUNT_PASSWORD_TOO_LONG)
    @Column(name = DatabaseConstants.ACCOUNTS_PASSWORD_COLUMN, nullable = false, length = 60)
    private String password;

    @NotNull(message = AccountConstants.ACCOUNT_ACTIVE_NULL)
    @Column(name = DatabaseConstants.ACCOUNTS_ACTIVE_COLUMN, nullable = false)
    private Boolean active = false;

    @NotNull(message = AccountConstants.ACCOUNT_ACTIVE_NULL)
    @Column(name = DatabaseConstants.ACCOUNTS_BLOCKED_COLUMN, nullable = false)
    private Boolean blocked = false;

    @NotBlank(message = AccountConstants.PERSONAL_DATA_FIRST_NAME_BLANK)
    @Size(min = AccountConstants.PERSONAL_DATA_FIRST_NAME_MIN_LENGTH, message = AccountConstants.PERSONAL_DATA_FIRST_NAME_TOO_SHORT)
    @Size(max = AccountConstants.PERSONAL_DATA_FIRST_NAME_MAX_LENGTH, message = AccountConstants.PERSONAL_DATA_FIRST_NAME_TOO_LONG)
    @Column(name = DatabaseConstants.PERSONAL_DATA_FIRST_NAME_COLUMN,
            table = DatabaseConstants.PERSONAL_DATA_TABLE, nullable = false)
    private String firstName;

    @NotBlank(message = AccountConstants.PERSONAL_DATA_LAST_NAME_BLANK)
    @Size(min = AccountConstants.PERSONAL_DATA_LAST_NAME_MIN_LENGTH, message = AccountConstants.PERSONAL_DATA_LAST_NAME_TOO_SHORT)
    @Size(max = AccountConstants.PERSONAL_DATA_LAST_NAME_MAX_LENGTH, message = AccountConstants.PERSONAL_DATA_LAST_NAME_TOO_LONG)
    @Column(name = DatabaseConstants.PERSONAL_DATA_LAST_NAME_COLUMN,
            table = DatabaseConstants.PERSONAL_DATA_TABLE, nullable = false)
    private String lastName;

    @Email
    @NotBlank(message = AccountConstants.PERSONAL_DATA_EMAIL_BLANK)
    @Size(min = AccountConstants.PERSONAL_DATA_EMAIL_MIN_LENGTH, message = AccountConstants.PERSONAL_DATA_EMAIL_TOO_SHORT)
    @Size(max = AccountConstants.PERSONAL_DATA_EMAIL_MAX_LENGTH, message = AccountConstants.PERSONAL_DATA_EMAIL_TOO_LONG)
    @Column(name = DatabaseConstants.PERSONAL_DATA_EMAIL_COLUMN,
            table = DatabaseConstants.PERSONAL_DATA_TABLE, nullable = false)
    private String email;

    @NotBlank(message = AccountConstants.PERSONAL_DATA_PHONE_NUMBER_BLANK)
    @Size(min = AccountConstants.PERSONAL_DATA_PHONE_NUMBER_MIN_LENGTH, message = AccountConstants.PERSONAL_DATA_PHONE_NUMBER_TOO_SHORT)
    @Size(max = AccountConstants.PERSONAL_DATA_PHONE_NUMBER_MAX_LENGTH, message = AccountConstants.PERSONAL_DATA_PHONE_NUMBER_TOO_LONG)
    @Column(name = DatabaseConstants.PERSONAL_DATA_PHONE_NUMBER_COLUMN,
            table = DatabaseConstants.PERSONAL_DATA_TABLE, nullable = false)
    private String phoneNumber;

    @Size(min = AccountConstants.ADDRESS_DATA_COUNTRY_NAME_MIN_LENGTH, message = AccountConstants.ADDRESS_DATA_COUNTRY_NAME_TOO_SHORT)
    @Size(max = AccountConstants.ADDRESS_DATA_COUNTRY_NAME_MAX_LENGTH, message = AccountConstants.ADDRESS_DATA_COUNTRY_NAME_TOO_LONG)
    @Column(name = DatabaseConstants.ADDRESS_DATA_COUNTRY_NAME_COLUMN,
            table = DatabaseConstants.ADDRESS_DATA_TABLE)
    private String countryName;

    @Size(min = AccountConstants.ADDRESS_DATA_PROVINCE_NAME_MIN_LENGTH, message = AccountConstants.ADDRESS_DATA_PROVINCE_NAME_TOO_SHORT)
    @Size(max = AccountConstants.ADDRESS_DATA_PROVINCE_NAME_MAX_LENGTH, message = AccountConstants.ADDRESS_DATA_PROVINCE_NAME_TOO_LONG)
    @Column(name = DatabaseConstants.ADDRESS_DATA_PROVINCE_NAME_COLUMN,
            table = DatabaseConstants.ADDRESS_DATA_TABLE)
    private String provinceName;

    @Size(min = AccountConstants.ADDRESS_DATA_CITY_NAME_MIN_LENGTH, message = AccountConstants.ADDRESS_DATA_CITY_NAME_TOO_SHORT)
    @Size(max = AccountConstants.ADDRESS_DATA_CITY_NAME_MAX_LENGTH, message = AccountConstants.ADDRESS_DATA_CITY_NAME_TOO_LONG)
    @Column(name = DatabaseConstants.ADDRESS_DATA_CITY_NAME_COLUMN,
            table = DatabaseConstants.ADDRESS_DATA_TABLE)
    private String cityName;

    @Size(min = AccountConstants.ADDRESS_DATA_STREET_NAME_MIN_LENGTH, message = AccountConstants.ADDRESS_DATA_STREET_NAME_TOO_SHORT)
    @Size(max = AccountConstants.ADDRESS_DATA_STREET_NAME_MAX_LENGTH, message = AccountConstants.ADDRESS_DATA_STREET_NAME_TOO_LONG)
    @Column(name = DatabaseConstants.ADDRESS_DATA_STREET_NAME_COLUMN,
            table = DatabaseConstants.ADDRESS_DATA_TABLE)
    private String streetName;

    @Positive(message = AccountConstants.ADDRESS_DATA_STREET_NUMBER_NEGATIVE)
    @Column(name = DatabaseConstants.ADDRESS_DATA_STREET_NUMBER_COLUMN,
            table = DatabaseConstants.ADDRESS_DATA_TABLE)
    private Integer streetNumber;

    @Size(min = AccountConstants.ADDRESS_DATA_ZIP_CODE_LENGTH, message = AccountConstants.ADDRESS_DATA_ZIP_CODE_TOO_SHORT)
    @Size(max = AccountConstants.ADDRESS_DATA_ZIP_CODE_LENGTH, message = AccountConstants.ADDRESS_DATA_ZIP_CODE_TOO_LONG)
    @Column(name = DatabaseConstants.ADDRESS_DATA_ZIP_CODE_COLUMN,
            table = DatabaseConstants.ADDRESS_DATA_TABLE)
    private String zipCode;

    @NotBlank(message = AccountConstants.ACCOUNT_LANGUAGE_BLANK)
    @Size(min = AccountConstants.ACCOUNT_LANGUAGE_LENGTH, message = AccountConstants.ACCOUNT_LANGUAGE_TOO_SHORT)
    @Size(max = AccountConstants.ACCOUNT_LANGUAGE_LENGTH, message = AccountConstants.ACCOUNT_LANGUAGE_TOO_LONG)
    @Column(name = DatabaseConstants.ACCOUNTS_LANGUAGE_COLUMN, nullable = false)
    private String language = "EN";

    @OneToMany(mappedBy = "account")
    private Set<AccessLevel> accessLevels = new HashSet<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = DatabaseConstants.ACCOUNTS_AVATAR_ID,
            referencedColumnName = DatabaseConstants.PK_COLUMN,
            foreignKey = @ForeignKey(name = DatabaseConstants.ACCOUNTS_AVATAR_ID_FK)
    )
    private UserFile userFile;

    // Constructor

    public Account(String login,
                   String password,
                   String firstName,
                   String lastName,
                   String email,
                   String phoneNumber,
                   String language) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.language = language;
    }

    // Other methods

    public void addAccessLevel(AccessLevel accessLevel) {
        if (this.accessLevels.contains(accessLevel)) {
            throw new AccessLevelGrantException();
        }
        accessLevel.setAccount(this);
        this.accessLevels.add(accessLevel);
    }

    public void removeAccessLevel(AccessLevel accessLevel) {
        if (!this.accessLevels.contains(accessLevel)) {
            throw new AccessLevelRevokeException();
        }
        accessLevel.setAccount(null);
        this.accessLevels.remove(accessLevel);
    }

    // Equal, hashCode & toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return new EqualsBuilder()
                .append(login, account.login)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(login)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(super.toString())
                .append("Login: ", login)
                .toString();
    }
}
