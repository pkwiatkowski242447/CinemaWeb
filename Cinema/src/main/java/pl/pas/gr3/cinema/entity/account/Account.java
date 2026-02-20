package pl.pas.gr3.cinema.entity.account;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.messages.validation.UserValidationMessages;

import java.util.UUID;

@Getter @Setter
@EqualsAndHashCode
@ToString
@BsonDiscriminator(key = UserConstants.USER_DISCRIMINATOR_NAME)
public abstract class Account {

    @BsonProperty(UserConstants.GENERAL_IDENTIFIER)
    @Setter(AccessLevel.NONE)
    @NotNull(message = UserValidationMessages.NULL_IDENTIFIER)
    protected UUID id;

    @BsonProperty(UserConstants.USER_LOGIN)
    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    protected String login;

    @BsonProperty(UserConstants.USER_PASSWORD)
    @NotNull(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    protected String password;

    @BsonProperty(UserConstants.USER_STATUS_ACTIVE)
    protected boolean active;

    @Setter(AccessLevel.NONE)
    protected Role role = null;
}
