package es.urjc.club_tenis.dto.user;

import es.urjc.club_tenis.dto.match.MatchDTO;

import java.sql.Blob;
import java.util.List;

public record UserBasicDTO (
        Long id,
        String username,
        String name,
        Blob profilePicture
){

}
