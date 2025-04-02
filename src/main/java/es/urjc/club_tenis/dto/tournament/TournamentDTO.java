package es.urjc.club_tenis.dto.tournament;

import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.dto.user.UserDTO;
import java.time.LocalDate;
import java.util.List;

public record TournamentDTO(
        Long id,
        String name,
        LocalDate initDate,
        LocalDate endDate,
        int price,
        List<UserDTO> participants,
        List<MatchDTO> matches
) {
}
