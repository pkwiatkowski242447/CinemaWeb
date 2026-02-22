package pl.pas.gr3.cinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.pas.gr3.cinema.dto.ticket.TicketResponse;
import pl.pas.gr3.cinema.entity.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "clientId", source = "userId")
    TicketResponse toResponse(Ticket ticket);
}
