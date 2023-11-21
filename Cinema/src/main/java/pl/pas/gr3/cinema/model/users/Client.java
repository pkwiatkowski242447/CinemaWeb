package pl.pas.gr3.cinema.model.users;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Client {

    @Setter(AccessLevel.NONE)
    protected UUID clientID;

    protected String clientLogin;

    protected String clientPassword;

    protected boolean clientStatusActive;

    // Constructors

    public Client() {
    }

    public Client(UUID clientID, String clientLogin, String clientPassword) {
        this.clientID = clientID;
        this.clientLogin = clientLogin;
        this.clientPassword = clientPassword;
        this.clientStatusActive = true;
    }

    // Equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return new EqualsBuilder()
                .append(clientID, client.clientID)
                .append(clientLogin, client.clientLogin)
                .append(clientPassword, client.clientPassword)
                .append(clientStatusActive, client.clientStatusActive)
                .isEquals();
    }

    // HashCode method

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(clientID)
                .append(clientLogin)
                .append(clientPassword)
                .append(clientStatusActive)
                .toHashCode();
    }

    // ToString method

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("User ID: ", clientID)
                .append("User login: ", clientLogin)
                .append("User password: ", clientPassword)
                .append("User status active: ", clientStatusActive)
                .toString();
    }
}
