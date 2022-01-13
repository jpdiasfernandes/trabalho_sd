package server.businesslogic.excecoes;

public class VooIndisponivelException extends Exception {
    public VooIndisponivelException() {
    }

    public VooIndisponivelException(String message) {
        super(message);
    }
}
