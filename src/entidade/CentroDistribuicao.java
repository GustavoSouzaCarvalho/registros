package entidade;

import java.util.Objects;
import java.util.UUID;

public class CentroDistribuicao {
    private UUID codigo;
    private String nome;
    private String endereco;
    private String CEP;
    private Integer limite = 1000;

    // Construtor padrão
    public CentroDistribuicao() {
        this.codigo = UUID.randomUUID();
    }

    // Construtor que aceita parâmetros e gera o UUID automaticamente
    public CentroDistribuicao(String nome, String endereco, String CEP) {
        this.codigo = UUID.randomUUID();
        this.nome = nome;
        this.endereco = endereco;
        this.CEP = CEP;
    }

    public UUID getCodigo() {
        return codigo;
    }

    public void setCodigo(UUID codigo) {
        this.codigo = codigo;
    }

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getLimite() {
        return limite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CentroDistribuicao that = (CentroDistribuicao) o;
        return Objects.equals(codigo, that.codigo) && Objects.equals(CEP, that.CEP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, CEP);
    }

    @Override
    public String toString() {
        return codigo +
                "/ " + nome +
                "/ " + endereco +
                "/ CEP: " + CEP;
    }

    public int compareTo(CentroDistribuicao ocd) {
        int fator = this.nome.compareTo(ocd.getNome());
        if (fator == 0) {
            fator = this.CEP.compareTo(ocd.CEP);
        }
        return fator;
    }
}
