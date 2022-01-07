package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.UsernameExistente;

public class GestaoLN {
    private ContasLN contas = new ContasLN();
    private VoosLN voos = new VoosLN();

    public void registarUtilizador(String username, String pwd) throws UsernameExistente {
        contas.registarUtilizador(username, pwd);
    }

    public boolean validarUtilizador(String username, String pwd) {
        return contas.validarUtilizador(username, pwd);
    }
}
