package logicaCadastro.logicaCentro;

import dados.CentroDAO;
import entidade.CentroDistribuicao;
import logicaCadastro.Cadastro;

import java.util.UUID;

public class LogicaCentro implements Cadastro<CentroDistribuicao> {
    private CentroDAO centroDAO;

    public LogicaCentro(CentroDAO centroDAO) {
        this.centroDAO = centroDAO;
    }

    @Override
    public void salvar(CentroDistribuicao aCadastrar) throws Exception {
        //ValidarCliente.validar(aCadastrar);
        centroDAO.inserir(aCadastrar);
    }

    @Override
    public CentroDistribuicao buscar(UUID codigo) {
        return null;
    }

    @Override
    public void deletar(UUID codigo) {
        centroDAO.deletar(String.valueOf(codigo));
    }

    @Override
    public void atualizar(CentroDistribuicao aAtualizar) {
        centroDAO.atualizar(aAtualizar);
    }

    @Override
    public void imprimir() {
        centroDAO.listar().forEach(System.out::println);
    }
}
