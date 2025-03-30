package demo.RealEstate.exception;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {

        super(message);
    }
}