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
    private String description;
    private String address;
    private Double price;

    @ElementCollection
    private List<String> imageFileNames;
    private String modelFileName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDAO user;
}
