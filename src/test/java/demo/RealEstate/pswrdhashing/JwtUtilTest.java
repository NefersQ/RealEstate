package demo.RealEstate.pswrdhashing;

import demo.RealEstate.jwt.JwtUtil;
import demo.RealEstate.model.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private UserDAO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDAO();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        String secureKey = "testSecretKeyJwtasefasefasefase12345678901234567890123456789012345678901234567890123456789012345";
        ReflectionTestUtils.setField(jwtUtil, "secret", secureKey);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        String token = jwtUtil.generateToken(testUser);
        Long userId = jwtUtil.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtUtil.generateToken(testUser);
        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        String invalidToken = "invalid.token.string";
        boolean isValid = jwtUtil.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -3600);

        String expiredToken = jwtUtil.generateToken(testUser);

        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600);

        boolean isValid = jwtUtil.validateToken(expiredToken);

        assertFalse(isValid);
    }
}