package pl.pas.gr3.cinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.pas.gr3.cinema.dto.output.TicketResponse;
import pl.pas.gr3.cinema.entity.Ticket;

@Mapper
public interface TicketMapper {

    @Mapping(target = "clientId", source = "userId")
    TicketResponse toResponse(Ticket ticket);
}
