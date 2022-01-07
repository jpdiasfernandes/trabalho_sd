package Server.BusinessLogic.Excecoes;

public class UsernameExistenteException extends Exception {
    public UsernameExistenteException() {
    }

    public UsernameExistenteException(String message) {
        super(message);
    }
}
