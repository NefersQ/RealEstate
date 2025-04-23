package demo.RealEstate.dto;

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
    private Double area;
    private Integer rooms;
    private String propertyType;
    private Integer totalFloors;
    private Integer floor;
    private Double pricePerSqm;
    private String phoneNumber;
    private String email;
    private String constructionType;
    private String district;
    private String modelFileUrl;
    private List<String> imageFileUrls;
}
