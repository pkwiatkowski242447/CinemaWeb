package pl.pas.gr3.cinema.entity.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;

import java.util.UUID;

@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BsonDiscriminator(key = UserConstants.USER_DISCRIMINATOR_NAME, value = UserConstants.ADMIN_DISCRIMINATOR)
public class Admin extends Account {

    public Admin(UUID id,
                 String login,
                 String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.active = true;
        this.role = Role.ADMIN;
    }

    @BsonCreator
    public Admin(@BsonProperty(UserConstants.GENERAL_IDENTIFIER) UUID id,
                 @BsonProperty(UserConstants.USER_LOGIN) String login,
                 @BsonProperty(UserConstants.USER_PASSWORD) String password,
                 @BsonProperty(UserConstants.USER_STATUS_ACTIVE) boolean active) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.active = active;
        this.role = Role.ADMIN;
    }
}
