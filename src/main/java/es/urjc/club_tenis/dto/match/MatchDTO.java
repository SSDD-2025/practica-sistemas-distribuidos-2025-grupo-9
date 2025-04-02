package es.urjc.club_tenis.dto.match;

import es.urjc.club_tenis.dto.user.UserDTO;
import es.urjc.club_tenis.dto.court.CourtDTO;

public record MatchDTO(
        Long id,
        UserDTO owner,
        UserDTO winner,
        UserDTO local,
        UserDTO visitor,
        CourtDTO court,
        String result) {
}