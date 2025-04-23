package demo.RealEstate.mapper;

import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.model.UserDAO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserDAO userDAO);

    List<UserDTO> toDTOList(List<UserDAO> userDAOs);
}
