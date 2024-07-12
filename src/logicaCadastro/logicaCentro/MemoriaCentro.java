package logicaCadastro.logicaCentro;

import entidade.CentroDistribuicao;
import logicaCadastro.Cadastro;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemoriaCentro implements Cadastro<CentroDistribuicao> {
    private List<CentroDistribuicao> lista;

    public MemoriaCentro(){
        this.lista = new ArrayList<>();

    }
    @Override
    public void salvar(CentroDistribuicao aCadastrar){ //throws CPFException, FotoNaoSelecionadaException
     //   ValidarCliente.validar(aCadastrar);
        this.lista.add(aCadastrar);
     //   GerenciadordeArquivo.arquivo(aCadastrar.getNome() + ".jpg", aCadastrar.getFoto());
    }

    @Override
    public CentroDistribuicao buscar(UUID codigo) {
        CentroDistribuicao encontrado = null;
        for(CentroDistribuicao c : this.lista){
            if(c.getCodigo().equals(codigo)){
                encontrado = c;
                break;
            }
        }
        return encontrado;
    }

    @Override
    public void deletar(UUID codigo) {
        CentroDistribuicao excluido = this.buscar(codigo);
        if(excluido != null){
            this.lista.remove(excluido);
        }
    }

    @Override
    public void atualizar(CentroDistribuicao aAtualizar) {

    }

    @Override
    public void imprimir() {
        System.out.println("Imprimindo " + this.lista.size() + " centros");
        lista.sort(CentroDistribuicao::compareTo);
        for(CentroDistribuicao c: this.lista){
            System.out.println(c);
        }
    }
}

