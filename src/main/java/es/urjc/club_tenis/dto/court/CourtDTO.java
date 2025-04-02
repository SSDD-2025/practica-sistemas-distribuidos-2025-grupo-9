package es.urjc.club_tenis.dto.court;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Map;
import es.urjc.club_tenis.dto.user.UserDTO;

public record CourtDTO(
        Long id,
        float price,
        String name,
        LocalTime start,
        LocalTime end,
        Map<LocalDateTime, UserDTO> reservations
    ) {
}
