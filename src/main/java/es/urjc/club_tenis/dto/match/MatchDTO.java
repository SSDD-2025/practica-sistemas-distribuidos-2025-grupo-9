package es.urjc.club_tenis.dto.match;

import es.urjc.club_tenis.dto.court.CourtBasicDTO;
import es.urjc.club_tenis.dto.user.UserBasicDTO;

public record MatchDTO(
        Long id,
        UserBasicDTO owner,
        UserBasicDTO winner,
        UserBasicDTO local,
        UserBasicDTO visitor,
        CourtBasicDTO court,
        String result) {
}