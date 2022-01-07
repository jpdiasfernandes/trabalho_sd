package Server.BusinessLogic;

import Server.BusinessLogic.Excecoes.NaoTemPermissaoException;
import Server.BusinessLogic.Excecoes.UsernameExistenteException;
import Server.BusinessLogic.Excecoes.UsernameNaoExistenteException;

public class GestaoLN {
    private ContasLN contas = new ContasLN();
    private VoosLN voos = new VoosLN();

    public void registarUtilizador(String username, String pwd) throws UsernameExistenteException {
        contas.registarUtilizador(username, pwd);
    }

    public boolean validarUtilizador(String username, String pwd) throws UsernameNaoExistenteException {
        return contas.validarUtilizador(username, pwd);
    }

    public void insercaoVoo(String username, String origem, String destino, int capacidade)
            throws NaoTemPermissaoException, UsernameNaoExistenteException {
        if (!contas.admin(username)) throw new NaoTemPermissaoException();
        voos.inserirVoo(origem, destino, capacidade);
    }


}
