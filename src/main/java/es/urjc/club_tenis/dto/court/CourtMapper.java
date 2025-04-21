package es.urjc.club_tenis.dto.court;

import es.urjc.club_tenis.model.Court;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CourtMapper {

    CourtDTO toDTO(Court court);

    CourtBasicDTO toBasicDTO(Court court);

    List<CourtBasicDTO> toDTOs(Collection<Court> courts);

    @Mapping(target = "reservations", ignore = true)
    Court toDomain(CourtBasicDTO courtBasicDTO);
}
