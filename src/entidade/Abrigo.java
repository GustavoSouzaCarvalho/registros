package entidade;

import java.util.Objects;
import java.util.UUID;

public class Abrigo {
    private UUID codigo;
    private String nome;
    private String endereco;
    private String responsavel;
    private String telefone;
    private String email;
    private Integer limite = 200;

    public Abrigo(){
        this.codigo = UUID.randomUUID();
    }

    public Abrigo(String nome, String endereco, String responsavel, String telefone, String email) {
        this.codigo = UUID.randomUUID();
        this.nome = nome;
        this.endereco = endereco;
        this.responsavel = responsavel;
        this.telefone = telefone;
        this.email = email;
    }

    public UUID getCodigo() {
        return codigo;
    }

    public void setCodigo(UUID codigo) {
        this.codigo = codigo;
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

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLimite() {
        return limite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Abrigo abrigo = (Abrigo) o;
        return Objects.equals(codigo, abrigo.codigo) && Objects.equals(nome, abrigo.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, nome);
    }
    @Override
    public String toString() {
        return codigo +
                "/ " + nome +
                "/ " + endereco +
                "/ Responsavel: " + responsavel +
                "/ " + telefone +
                "/ " + email;
    }
    public int compareTo(Abrigo oa){
        int fator = this.nome.compareTo(oa.getNome());
        if(fator == 0){
            fator = this.codigo.compareTo(oa.codigo);
        }
        return fator;
    }
}
