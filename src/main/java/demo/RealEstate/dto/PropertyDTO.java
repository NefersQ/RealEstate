package demo.RealEstate.dto;

import demo.RealEstate.model.PropertyDAO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PropertyDTO {
    private Long id;
    private String title;
    private String description;
    private String address;
    private Double price;
    private String modelFileUrl;
    private List<String> imageFileUrls;

    public static PropertyDTO from(PropertyDAO property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setAddress(property.getAddress());
        dto.setPrice(property.getPrice());

        if (property.getModelFileName() != null) {
            dto.setModelFileUrl("/models/" + property.getModelFileName());
        }

        if (property.getImageFileNames() != null) {
            dto.setImageFileUrls(
                    property.getImageFileNames().stream()
                            .map(fileName -> "/images/" + fileName)
                            .toList()
            );
        }

        return dto;
    }
}
