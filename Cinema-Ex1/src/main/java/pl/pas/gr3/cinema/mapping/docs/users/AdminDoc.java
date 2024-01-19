package pl.pas.gr3.cinema.mapping.docs.users;

import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@NoArgsConstructor
@BsonDiscriminator(key = "_clazz", value = "admin")
public class AdminDoc extends ClientDoc {
    @BsonCreator
    public AdminDoc(@BsonProperty("_id") UUID adminID,
                    @BsonProperty("client_login") String adminLogin,
                    @BsonProperty("client_password") String adminPassword,
                    @BsonProperty("client_status_active") boolean adminStatusActive) {
        this.clientID = adminID;
        this.clientLogin = adminLogin;
        this.clientPassword = adminPassword;
        this.clientStatusActive = adminStatusActive;
    }
}
