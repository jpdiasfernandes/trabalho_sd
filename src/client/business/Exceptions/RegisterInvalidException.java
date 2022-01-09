package business.Exceptions;

public class RegisterInvalidException extends Exception{
    public RegisterInvalidException() {
    }

    public RegisterInvalidException(String message) {
        super(message);
    }
}
