package entidade;

import produtos.TamanhoRoupa;
import produtos.TipoAlimento;
import produtos.TipoHigiene;
import produtos.TipoProduto;

import java.util.Date;
import java.util.UUID;

public class EstoqueCentro {
    private String codigo;
    private UUID id_estoqueCentro;
    private TipoProduto tipoProduto;
    private TipoHigiene tipoHigiene;
    private TipoAlimento tipoAlimento;
    private TamanhoRoupa tamanhoRoupa;
    private Integer quantidade;
    private Date validade;
    private Integer limite = 1000;

    public EstoqueCentro() {
        this.id_estoqueCentro = UUID.randomUUID();
    }

    public EstoqueCentro(String codigo, TipoProduto tipoProduto, Object especificacaoProduto, Integer quantidade, Date validade) {
        this.codigo = codigo;
        this.id_estoqueCentro = UUID.randomUUID();
        this.tipoProduto = tipoProduto;
        this.quantidade = quantidade;
        this.validade = validade;

        switch (tipoProduto) {
            case HIGIENE:
                this.tipoHigiene = (TipoHigiene) especificacaoProduto;
                break;
            case ALIMENTO:
                this.tipoAlimento = (TipoAlimento) especificacaoProduto;
                break;
            case ROUPA:
                this.tamanhoRoupa = (TamanhoRoupa) especificacaoProduto;
                break;
            default:
                throw new IllegalArgumentException("Tipo de produto inválido");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public UUID getId_estoqueCentro() {
        return id_estoqueCentro;
    }

    public void setId_estoqueCentro(UUID id_estoqueCentro) {
        this.id_estoqueCentro = id_estoqueCentro;
    }

    public TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public TipoHigiene getTipoHigiene() {
        return tipoHigiene;
    }

    public void setTipoHigiene(TipoHigiene tipoHigiene) {
        this.tipoHigiene = tipoHigiene;
    }

    public TipoAlimento getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(TipoAlimento tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public TamanhoRoupa getTamanhoRoupa() {
        return tamanhoRoupa;
    }

    public void setTamanhoRoupa(TamanhoRoupa tamanhoRoupa) {
        this.tamanhoRoupa = tamanhoRoupa;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getLimite() {
        return limite;
    }
    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    @Override
    public String toString() {
        String especificacaoProduto = "";
        switch (tipoProduto) {
            case HIGIENE:
                especificacaoProduto = " " + tipoHigiene;
                break;
            case ALIMENTO:
                especificacaoProduto = " " + tipoAlimento;
                break;
            case ROUPA:
                especificacaoProduto = " " + tamanhoRoupa;
                break;
        }

        return "Código: " + codigo +
                " / Tipo: " + tipoProduto +
                " /" + especificacaoProduto +
                " / Em estoque: " + quantidade +
                " / Validade: " + validade;
    }
}


