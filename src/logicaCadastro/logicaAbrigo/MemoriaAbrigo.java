package logicaCadastro.logicaAbrigo;

import entidade.Abrigo;
import entidade.CentroDistribuicao;
import logicaCadastro.Cadastro;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemoriaAbrigo implements Cadastro<Abrigo> {
    private List<Abrigo> lista;

    public MemoriaAbrigo(){
        this.lista = new ArrayList<>();

    }
    @Override
    public void salvar(Abrigo aCadastrar){ //throws CPFException, FotoNaoSelecionadaException
        //   ValidarCliente.validar(aCadastrar);
        this.lista.add(aCadastrar);
        //   GerenciadordeArquivo.arquivo(aCadastrar.getNome() + ".jpg", aCadastrar.getFoto());
    }

    @Override
    public Abrigo buscar(UUID codigo) {
        Abrigo encontrado = null;
        for(Abrigo a : this.lista){
            if(a.getCodigo().equals(codigo)){
                encontrado = a;
                break;
            }
        }
        return encontrado;
    }

    @Override
    public void deletar(UUID codigo) {
        Abrigo excluido = this.buscar(codigo);
        if(excluido != null){
            this.lista.remove(excluido);
        }
    }

    @Override
    public void atualizar(Abrigo aAtualizar) {

    }

    @Override
    public void imprimir() {
        System.out.println("Imprimindo " + this.lista.size() + " abrigos");
        lista.sort(Abrigo::compareTo);
        for(Abrigo a: this.lista){
            System.out.println(a);
        }
    }
}
