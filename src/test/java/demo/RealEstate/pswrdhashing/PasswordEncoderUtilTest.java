package demo.RealEstate.pswrdhashing;

import demo.RealEstate.config.PasswordEncoderUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncoderUtilTest {

    @Test
    void encodePassword_ShouldEncodePassword() {
        String rawPassword = "TestPassword123!";
        String encodedPassword = PasswordEncoderUtil.encodePassword(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    void matches_ShouldReturnTrue_WhenPasswordMatches() {
        String rawPassword = "TestPassword123!";
        String encodedPassword = PasswordEncoderUtil.encodePassword(rawPassword);

        boolean result = PasswordEncoderUtil.matches(rawPassword, encodedPassword);

        assertTrue(result);
    }

    @Test
    void matches_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        String rawPassword = "TestPassword123!";
        String wrongPassword = "WrongPassword123!";
        String encodedPassword = PasswordEncoderUtil.encodePassword(rawPassword);

        boolean result = PasswordEncoderUtil.matches(wrongPassword, encodedPassword);

        assertFalse(result);
    }

    @Test
    void encodePassword_ShouldGenerateDifferentHashesForSamePassword() {
        String password = "TestPassword123!";

        String firstHash = PasswordEncoderUtil.encodePassword(password);
        String secondHash = PasswordEncoderUtil.encodePassword(password);

        assertNotEquals(firstHash, secondHash);
    }
}