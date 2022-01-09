package business.Exceptions;

public class LoginInvalidException extends Exception{
    public LoginInvalidException() {
    }

    public LoginInvalidException(String message) {
        super(message);
    }
}
