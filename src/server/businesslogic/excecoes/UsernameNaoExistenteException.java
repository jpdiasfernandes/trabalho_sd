package server.businesslogic.excecoes;

public class UsernameNaoExistenteException extends Exception {
    public UsernameNaoExistenteException() {
    }

    public UsernameNaoExistenteException(String message) {
        super(message);
    }
}
