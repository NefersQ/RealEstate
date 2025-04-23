package demo.RealEstate.dto;

import demo.RealEstate.model.PropertyDAO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyDTO {
    private Long id;
    private String title;
    private String description;
    private String address;
    private Double price;
    private String modelFileUrl;

    public static PropertyDTO from(PropertyDAO property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setAddress(property.getAddress());
        dto.setPrice(property.getPrice());
        dto.setModelFileUrl("/models/" + property.getModelFileName());
        return dto;
    }
}
