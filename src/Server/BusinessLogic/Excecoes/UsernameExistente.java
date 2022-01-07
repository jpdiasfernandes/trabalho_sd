package Server.BusinessLogic.Excecoes;

public class UsernameExistente extends Exception {
    public UsernameExistente() {
    }

    public UsernameExistente(String message) {
        super(message);
    }
}
