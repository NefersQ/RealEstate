package demo.RealEstate.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTO {
    private Long userId;
    private String name;
    private String surname;
    private String username;
    private String email;
    private LocalDateTime userCreated = LocalDateTime.now();
}