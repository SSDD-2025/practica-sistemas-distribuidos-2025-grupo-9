package es.urjc.club_tenis.dto.user;

import es.urjc.club_tenis.model.User;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toDomain(UserDTO user);

    List<UserDTO> toDTOs(Collection<User> users);

}
