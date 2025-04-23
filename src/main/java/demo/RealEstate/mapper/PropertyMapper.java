package demo.RealEstate.mapper;


import demo.RealEstate.dto.PropertyDTO;
import demo.RealEstate.model.PropertyDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "propertyType", source = "propertyType", qualifiedByName = "enumToString")
    @Mapping(target = "constructionType", source = "constructionType", qualifiedByName = "enumToString")
    @Mapping(target = "district", source = "district", qualifiedByName = "enumToString")
    @Mapping(target = "modelFileUrl", source = "modelFileName", qualifiedByName = "toModelFileUrl")
    @Mapping(target = "imageFileUrls", source = "imageFileNames", qualifiedByName = "toImageFileUrls")
    PropertyDTO toDTO(PropertyDAO property);

    List<PropertyDTO> toDTOList(List<PropertyDAO> properties);

    @Named("enumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }

    @Named("toModelFileUrl")
    default String toModelFileUrl(String modelFileName) {
        return modelFileName != null ? "/models/" + modelFileName : null;
    }

    @Named("toImageFileUrls")
    default List<String> toImageFileUrls(List<String> imageFileNames) {
        if (imageFileNames == null) {
            return null;
        }
        return imageFileNames.stream()
                .map(fileName -> "/images/" + fileName)
                .toList();
    }
}
