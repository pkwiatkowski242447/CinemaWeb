package pl.pas.gr3.cinema.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import pl.pas.gr3.cinema.util.consts.model.TicketConstants;
import pl.pas.gr3.cinema.messages.validation.TicketValidationMessages;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Ticket {

    @BsonProperty(TicketConstants.GENERAL_IDENTIFIER)
    @NotNull(message = TicketValidationMessages.NULL_IDENTIFIER)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @BsonProperty(TicketConstants.MOVIE_TIME)
    private LocalDateTime movieTime;

    @BsonProperty(TicketConstants.TICKET_FINAL_PRICE)
    @PositiveOrZero(message = TicketValidationMessages.INVALID_TICKET_FINAL_PRICE)
    @Setter(AccessLevel.NONE)
    private double price;

    @BsonProperty(TicketConstants.USER_ID)
    @NotNull(message = TicketValidationMessages.NULL_CLIENT_REFERENCE)
    @Setter(AccessLevel.NONE)
    private UUID userId;

    @BsonProperty(TicketConstants.MOVIE_ID)
    @NotNull(message = TicketValidationMessages.NULL_MOVIE_REFERENCE)
    @Setter(AccessLevel.NONE)
    private UUID movieId;

    /* Constructors */

    @BsonCreator
    public Ticket(@BsonProperty(TicketConstants.GENERAL_IDENTIFIER) UUID id,
                  @BsonProperty(TicketConstants.MOVIE_TIME) LocalDateTime movieTime,
                  @BsonProperty(TicketConstants.TICKET_FINAL_PRICE) double price,
                  @BsonProperty(TicketConstants.USER_ID) UUID userId,
                  @BsonProperty(TicketConstants.MOVIE_ID) UUID movieId) {
        this.id = id;
        this.movieTime = movieTime;
        this.price = price;
        this.userId = userId;
        this.movieId = movieId;
    }

    /* Other methods */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return new EqualsBuilder()
                .append(id, ticket.id)
                .append(movieTime, ticket.movieTime)
                .append(price, ticket.price)
                .append(userId, ticket.userId)
                .append(movieId, ticket.movieId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(movieTime)
                .append(price)
                .append(userId)
                .append(movieId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("Ticket ID: ", id)
                .append("Movie time: ", movieTime)
                .append("Ticket final price: ", price)
                .append("Client: ", userId.toString())
                .append("Movie: ", movieId.toString())
                .toString();
    }
}
