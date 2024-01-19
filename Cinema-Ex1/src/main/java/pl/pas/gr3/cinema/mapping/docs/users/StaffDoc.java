package pl.pas.gr3.cinema.mapping.docs.users;

import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@NoArgsConstructor
@BsonDiscriminator(key = "_clazz", value = "staff")
public class StaffDoc extends ClientDoc {
    @BsonCreator
    public StaffDoc(@BsonProperty("_id") UUID staffID,
                    @BsonProperty("client_login") String staffLogin,
                    @BsonProperty("client_password") String staffPassword,
                    @BsonProperty("client_status_active") boolean staffStatusActive) {
        this.clientID = staffID;
        this.clientLogin = staffLogin;
        this.clientPassword = staffPassword;
        this.clientStatusActive = staffStatusActive;
    }
}
