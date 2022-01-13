package server.businesslogic.excecoes;

public class NaoTemPermissaoException extends Exception {
    public NaoTemPermissaoException() {
    }

    public NaoTemPermissaoException(String message) {
        super(message);
    }
}
