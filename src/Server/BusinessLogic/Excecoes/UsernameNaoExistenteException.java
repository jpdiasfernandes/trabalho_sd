package Server.BusinessLogic.Excecoes;

public class UsernameNaoExistenteException extends Exception {
    public UsernameNaoExistenteException() {
    }

    public UsernameNaoExistenteException(String message) {
        super(message);
    }
}
