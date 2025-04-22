package es.urjc.club_tenis.dto.match;

import es.urjc.club_tenis.dto.court.CourtBasicDTO;
import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.user.UserBasicDTO;

public record MatchDTO(
        Long id,
        UserBasicDTO owner,
        UserBasicDTO winner,
        UserBasicDTO local,
        UserBasicDTO visitor,
        CourtDTO court,
        String result) {
}