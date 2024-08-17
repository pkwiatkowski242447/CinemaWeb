package pl.pas.gr3.cinema.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.utils.constants.DatabaseConstants;
import pl.pas.gr3.cinema.utils.messages.TicketConstants;

@Entity
@Table(
        name = DatabaseConstants.TICKETS_TABLE,
        indexes = {
                @Index(name = DatabaseConstants.TICKETS_CLIENT_ID_IDX,
                        columnList = DatabaseConstants.TICKETS_CLIENT_ID),
                @Index(name = DatabaseConstants.TICKETS_SHOWING_ID_IDX,
                        columnList = DatabaseConstants.TICKETS_SHOWING_ID)
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket extends AbstractEntity {

    @PositiveOrZero(message = TicketConstants.TICKET_PRICE_NEGATIVE)
    @Column(name = DatabaseConstants.TICKETS_PRICE_COLUMN, nullable = false)
    private Double price;

    @NotNull(message = TicketConstants.TICKET_CLIENT_NULL)
    @ManyToOne
    @JoinColumn(
            name = DatabaseConstants.TICKETS_CLIENT_ID,
            referencedColumnName = DatabaseConstants.PK_COLUMN,
            foreignKey = @ForeignKey(name = DatabaseConstants.TICKETS_CLIENT_ID_FK)
    )
    private Client client;

    @NotNull(message = TicketConstants.TICKET_SHOWING_NULL)
    @ManyToOne
    @JoinColumn(
            name = DatabaseConstants.TICKETS_SHOWING_ID,
            referencedColumnName = DatabaseConstants.PK_COLUMN,
            foreignKey = @ForeignKey(name = DatabaseConstants.TICKETS_SHOWING_ID_FK)
    )
    private Showing showing;
}
