package logicaCadastro.logicaAbrigo;

import entidade.Abrigo;
import entidade.CentroDistribuicao;
import exception.CEPException;
import exception.DadoObrigatorioException;
import exception.TelefoneException;

public class ValidarAbrigo {
    public static void validar(Abrigo abrigo) throws TelefoneException {
        if (abrigo.getTelefone().length() != 11) {
            throw new TelefoneException("Telefone inválido");
        }
        dadosObrigatotios(abrigo);

    }
    private static void dadosObrigatotios(Abrigo abrigo){
        if(abrigo.getNome() == null || abrigo.getEndereco() == null || abrigo.getResponsavel() == null || abrigo.getTelefone() == null){
            throw new DadoObrigatorioException("Campo obrigatório vazio!");
        }
    }
}
