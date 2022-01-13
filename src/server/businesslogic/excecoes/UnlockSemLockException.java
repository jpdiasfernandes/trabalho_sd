package businesslogic.excecoes;

public class UnlockSemLockException extends Exception{
    public UnlockSemLockException() {
    }

    public UnlockSemLockException(String message) {
        super(message);
    }
}
