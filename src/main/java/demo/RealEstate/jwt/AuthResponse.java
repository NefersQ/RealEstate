package demo.RealEstate.jwt;

import demo.RealEstate.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private UserDTO user;
    private String token;

    public AuthResponse(UserDTO user, String token) {
        this.user = user;
        this.token = token;
    }
}
