package es.urjc.club_tenis.dto.court;

import java.time.LocalTime;

public record CourtBasicDTO(
        Long id,
        float price,
        String name
    ) {
}
