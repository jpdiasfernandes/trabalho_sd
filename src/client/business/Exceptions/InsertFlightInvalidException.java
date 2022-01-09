package business.Exceptions;

public class InsertFlightInvalidException extends Exception{
    public InsertFlightInvalidException() {
    }

    public InsertFlightInvalidException(String message) {
        super(message);
    }
}
