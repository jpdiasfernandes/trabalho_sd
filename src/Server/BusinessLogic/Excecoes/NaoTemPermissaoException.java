package Server.BusinessLogic.Excecoes;

public class NaoTemPermissaoException extends Exception {
    public NaoTemPermissaoException() {
    }

    public NaoTemPermissaoException(String message) {
        super(message);
    }
}
