package client.business.Exceptions;

public class CancelDayInvalidException extends Exception{
    public CancelDayInvalidException() {
    }

    public CancelDayInvalidException(String message) {
        super(message);
    }
}
