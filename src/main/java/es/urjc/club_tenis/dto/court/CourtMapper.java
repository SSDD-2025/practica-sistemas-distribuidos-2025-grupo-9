package es.urjc.club_tenis.dto.court;

import es.urjc.club_tenis.model.Court;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CourtMapper {

    CourtDTO toDTO(Court court);

    List<CourtDTO> toDTOs(Collection<Court> courts);

    Court toDomain(CourtDTO courtDTO);
}
