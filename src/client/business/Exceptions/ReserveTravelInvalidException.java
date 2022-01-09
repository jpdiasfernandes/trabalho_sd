package business.Exceptions;

public class ReserveTravelInvalidException extends Exception{
    public ReserveTravelInvalidException() {
    }

    public ReserveTravelInvalidException(String message) {
        super(message);
    }
}
