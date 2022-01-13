package businesslogic.excecoes;

public class UsernameExistenteException extends Exception {
    public UsernameExistenteException() {
    }

    public UsernameExistenteException(String message) {
        super(message);
    }
}
