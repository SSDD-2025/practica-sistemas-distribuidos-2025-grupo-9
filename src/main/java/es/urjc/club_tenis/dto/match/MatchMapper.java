package es.urjc.club_tenis.dto.match;

import es.urjc.club_tenis.model.TennisMatch;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    MatchDTO toDTO(TennisMatch match);

    Collection<MatchDTO> toDTOs(Collection<TennisMatch> matches);

    TennisMatch toDomain(MatchDTO match);

    List<TennisMatch> toDomain(Collection<MatchDTO> matches);
}
