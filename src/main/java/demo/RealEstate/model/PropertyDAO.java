package demo.RealEstate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "properties")
public class PropertyDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 5000)
    private String description;
    private String address;
    private Double price;
    private Double area;
    private Integer rooms;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    private Integer totalFloors;
    private Integer floor;

    private Double pricePerSqm;

    private String phoneNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private ConstructionType constructionType;

    @Enumerated(EnumType.STRING)
    private District district;

    private String modelFileName;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> imageFileNames;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDAO user;

    public enum PropertyType {
        HOUSE,
        APARTMENT
    }

    public enum ConstructionType {
        BRICK,
        PANEL,
        MONOLITH
    }

    public enum District {
        CENTER,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
