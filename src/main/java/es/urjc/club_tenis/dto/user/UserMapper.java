package es.urjc.club_tenis.dto.user;

import es.urjc.club_tenis.model.User;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    UserBasicDTO toBasicDTO(User user);

    List<UserBasicDTO> toDTOs(Collection<User> users);

    //User toDomain(UserDTO user);
    @Mapping(target = "playedTennisMatches", ignore = true)
    User toDomain(UserBasicDTO userDTO);
}
