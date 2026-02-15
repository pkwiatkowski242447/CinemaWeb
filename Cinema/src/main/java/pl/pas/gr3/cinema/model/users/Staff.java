package pl.pas.gr3.cinema.model.users;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.pas.gr3.cinema.consts.model.UserConstants;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@BsonDiscriminator(key = UserConstants.USER_DISCRIMINATOR_NAME, value = UserConstants.STAFF_DISCRIMINATOR)
public class Staff extends User {

    public Staff() {
    }

    public Staff(UUID clientID,
                 String clientLogin,
                 String clientPassword) {
        this.userID = clientID;
        this.userLogin = clientLogin;
        this.userPassword = clientPassword;
        this.userStatusActive = true;
        this.userRole = Role.STAFF;
    }

    @BsonCreator
    public Staff(@BsonProperty(UserConstants.GENERAL_IDENTIFIER) UUID clientID,
                 @BsonProperty(UserConstants.USER_LOGIN) String clientLogin,
                 @BsonProperty(UserConstants.USER_PASSWORD) String clientPassword,
                 @BsonProperty(UserConstants.USER_STATUS_ACTIVE) boolean clientStatusActive) {
        this.userID = clientID;
        this.userLogin = clientLogin;
        this.userPassword = clientPassword;
        this.userStatusActive = clientStatusActive;
        this.userRole = Role.STAFF;
    }
}
