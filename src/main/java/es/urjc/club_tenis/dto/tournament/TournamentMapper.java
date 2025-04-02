package es.urjc.club_tenis.dto.tournament;

import es.urjc.club_tenis.model.Tournament;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    TournamentDTO toDto(Tournament t);

    Tournament toDomain(TournamentDTO t);

    List<TournamentDTO> toDTOs(Collection<Tournament> tournaments);

    List<Tournament> toDomain(Collection<TournamentDTO> tournaments);

}
