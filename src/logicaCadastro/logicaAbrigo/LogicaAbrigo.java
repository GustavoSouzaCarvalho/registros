package logicaCadastro.logicaAbrigo;

import dados.AbrigoDAO;
import dados.CentroDAO;
import entidade.Abrigo;
import entidade.CentroDistribuicao;
import logicaCadastro.Cadastro;

import java.util.UUID;

public class LogicaAbrigo implements Cadastro<Abrigo> {
    private AbrigoDAO abrigoDAO;

    public LogicaAbrigo(AbrigoDAO abrigoDAO) {
        this.abrigoDAO = abrigoDAO;
    }

    @Override
    public void salvar(Abrigo aCadastrar) throws Exception {
        //ValidarCliente.validar(aCadastrar);
        abrigoDAO.inserir(aCadastrar);
    }

    @Override
    public Abrigo buscar(UUID codigo) {
        return abrigoDAO.buscar(codigo);
    }

    @Override
    public void deletar(UUID codigo) {
        abrigoDAO.deletar(String.valueOf(codigo));
    }

    @Override
    public void atualizar(Abrigo aAtualizar) {
        abrigoDAO.atualizar(aAtualizar);
    }

    @Override
    public void imprimir() {
        abrigoDAO.listar().forEach(System.out::println);
    }
}

