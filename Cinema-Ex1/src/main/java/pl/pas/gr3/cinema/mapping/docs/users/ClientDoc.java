package pl.pas.gr3.cinema.mapping.docs.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@BsonDiscriminator(key = "_clazz", value = "client")
public class ClientDoc {

    @BsonProperty("_id")
    protected UUID clientID;

    @BsonProperty("client_login")
    protected String clientLogin;

    @BsonProperty("client_password")
    protected String clientPassword;

    @BsonProperty("client_status_active")
    protected boolean clientStatusActive;

    @BsonCreator
    public ClientDoc (@BsonProperty("_id") UUID clientID,
                      @BsonProperty("client_login") String clientLogin,
                      @BsonProperty("client_password") String clientPassword,
                      @BsonProperty("client_status_active") boolean clientStatusActive) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
        this.clientStatusActive = clientStatusActive;
    }
}
