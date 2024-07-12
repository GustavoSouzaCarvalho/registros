package logicaCadastro.logicaCentro;

import entidade.CentroDistribuicao;
import exception.CEPException;
import exception.DadoObrigatorioException;

public class ValidarCentroDistribuicao {
    public static void validar(CentroDistribuicao centro) throws CEPException {
        if (centro.getCEP().length() != 11) {
            throw new CEPException("CEP inválido");
        }
          dadosObrigatotios(centro);
        //logicaIdade(cliente);
       // fotoNaoEnviada(cliente);
    }
    private static void dadosObrigatotios(CentroDistribuicao centro){
        if(centro.getNome() == null || centro.getEndereco() == null || centro.getCEP() == null){
            throw new DadoObrigatorioException("Campo obrigatório vazio!");
        }
    }
}

