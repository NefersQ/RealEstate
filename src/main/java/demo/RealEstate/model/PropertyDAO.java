package demo.RealEstate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String modelFileName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDAO user;
}
