package logicaCadastro;

import java.util.UUID;

public interface Cadastro<Tipo> {
    void salvar(Tipo aCadastrar) throws Exception;

    Tipo buscar(UUID codigo);

    void deletar(UUID codigo);

    void atualizar(Tipo aAtualizar);

    void imprimir();
}
