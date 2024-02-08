package pl.pas.gr3.cinema.model.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.pas.gr3.cinema.consts.model.UserConstants;
import pl.pas.gr3.cinema.messages.validation.UserValidationMessages;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@BsonDiscriminator(key = UserConstants.USER_DISCRIMINATOR_NAME)
public abstract class User {

    @BsonProperty(UserConstants.GENERAL_IDENTIFIER)
    @Setter(AccessLevel.NONE)
    @NotNull(message = UserValidationMessages.NULL_IDENTIFIER)
    protected UUID userID;

    @BsonProperty(UserConstants.USER_LOGIN)
    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    protected String userLogin;

    @BsonProperty(UserConstants.USER_PASSWORD)
    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    protected String userPassword;

    @BsonProperty(UserConstants.USER_STATUS_ACTIVE)
    protected boolean userStatusActive;

    @Setter(AccessLevel.NONE)
    protected Role userRole = null;

    // Other methods

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(userID, user.userID)
                .append(userLogin, user.userLogin)
                .append(userPassword, user.userPassword)
                .append(userStatusActive, user.userStatusActive)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userID)
                .append(userLogin)
                .append(userPassword)
                .append(userStatusActive)
                .toHashCode();
    }
}
