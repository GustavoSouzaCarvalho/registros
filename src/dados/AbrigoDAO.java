package dados;

import entidade.Abrigo;
import entidade.EstoqueAbrigo;
import produtos.TamanhoRoupa;
import produtos.TipoAlimento;
import produtos.TipoHigiene;
import produtos.TipoProduto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AbrigoDAO {
    private Connection conexao;

    public AbrigoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Abrigo abrigo){
        try {
            String comandoSQL = """
                    insert into abrigo (codigo, nome, endereco, responsavel, telefone, email)
                    values(?, ?, ?, ?, ?, ?)
                    
                    """;
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, String.valueOf(abrigo.getCodigo()));
            comando.setString(2, abrigo.getNome());
            comando.setString(3, abrigo.getEndereco());
            comando.setString(4, abrigo.getResponsavel());
            comando.setString(5, abrigo.getTelefone());
            comando.setString(6, abrigo.getEmail());

            comando.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public Abrigo buscar(UUID codigo) {
        try {
            String comandoSQL = "SELECT * FROM abrigo WHERE codigo = ?";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, codigo.toString());
            ResultSet resultado = comando.executeQuery();

            if (resultado.next()) {
                String nome = resultado.getString("nome");
                String endereco = resultado.getString("endereco");
                String responsavel = resultado.getString("responsavel");
                String telefone = resultado.getString("telefone");
                String email = resultado.getString("email");

                Abrigo abrigo = new Abrigo();
                abrigo.setCodigo(codigo);
                abrigo.setNome(nome);
                abrigo.setEndereco(endereco);
                abrigo.setResponsavel(responsavel);
                abrigo.setTelefone(telefone);
                abrigo.setEmail(email);

                return abrigo;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Abrigo abrigo) {
        try {
            StringBuilder comandoSQL = new StringBuilder("update abrigo set ");
            List<Object> parametros = new ArrayList<>();

            if (abrigo.getNome() != null) {
                comandoSQL.append("nome = ?, ");
                parametros.add(abrigo.getNome());
            }
            if (abrigo.getEndereco() != null) {
                comandoSQL.append("endereco = ?, ");
                parametros.add(abrigo.getEndereco());
            }
            if (abrigo.getResponsavel() != null) {
                comandoSQL.append("responsavel = ?, ");
                parametros.add(abrigo.getResponsavel());
            }
            if (abrigo.getTelefone() != null) {
                comandoSQL.append("telefone = ?, ");
                parametros.add(abrigo.getTelefone());
            }
            if (abrigo.getEmail() != null) {
                comandoSQL.append("email = ?, ");
                parametros.add(abrigo.getEmail());
            }


            // Remove a última vírgula e espaço
            comandoSQL.setLength(comandoSQL.length() - 2);
            comandoSQL.append(" where codigo = ?");
            parametros.add(abrigo.getCodigo());

            PreparedStatement comando = conexao.prepareStatement(comandoSQL.toString());
            for (int i = 0; i < parametros.size(); i++) {
                comando.setObject(i + 1, parametros.get(i));
            }

            comando.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deletar(String codigo) {
        try {
            String comandoSQL = "delete from abrigo where codigo = ?";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, codigo);
            comando.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Abrigo> listar(){
        try {
            String comandoSQL = "select * from abrigo";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);

            ResultSet resultado = comando.executeQuery();

            List<Abrigo> lista = new ArrayList<>();

            while(resultado.next()) {
                String codigo = resultado.getString("codigo");
                String nome = resultado.getString("nome");
                String endereco = resultado.getString("endereco");
                String responsavel = resultado.getString("responsavel");
                String telefone = resultado.getString("telefone");
                String email = resultado.getString("email");



                Abrigo abrigo = new Abrigo();

                abrigo.setCodigo(UUID.fromString(codigo));
                abrigo.setNome(nome);
                abrigo.setEndereco(endereco);
                abrigo.setResponsavel(responsavel);
                abrigo.setTelefone(telefone);
                abrigo.setEmail(email);


                lista.add(abrigo);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<EstoqueAbrigo> listarEstoqueAbrigo(String codigoAbrigo) {
        try {
            String comandoSQL = "SELECT tipoProduto, produto, quantidade FROM estoqueAbrigo WHERE abrigo = ?";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, codigoAbrigo);

            ResultSet resultado = comando.executeQuery();

            List<EstoqueAbrigo> listaEstoque = new ArrayList<>();

            while (resultado.next()) {
                TipoProduto tipoProduto = TipoProduto.valueOf(resultado.getString("tipoProduto"));
                String produto = resultado.getString("produto");
                int quantidade = resultado.getInt("quantidade");

                EstoqueAbrigo estoque = new EstoqueAbrigo();
                estoque.setCodigo(codigoAbrigo);
                estoque.setTipoProduto(tipoProduto);
                estoque.setQuantidade(quantidade);

                switch (tipoProduto) {
                    case HIGIENE:
                        estoque.setTipoHigiene(TipoHigiene.valueOf(produto));
                        break;
                    case ALIMENTO:
                        estoque.setTipoAlimento(TipoAlimento.valueOf(produto));
                        break;
                    case ROUPA:
                        estoque.setTamanhoRoupa(TamanhoRoupa.valueOf(produto));
                        break;
                }

                listaEstoque.add(estoque);
            }
            return listaEstoque;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, Integer> listarEstoqueAgrupado(String codigoAbrigo) {
        try {
            String comandoSQL = "SELECT tipoProduto, produto, SUM(quantidade) as quantidade FROM estoqueAbrigo WHERE abrigo = ? GROUP BY tipoProduto, produto";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, codigoAbrigo);

            ResultSet resultado = comando.executeQuery();

            Map<String, Integer> estoqueAgrupado = new HashMap<>();

            while (resultado.next()) {
                String tipoProduto = resultado.getString("tipoProduto");
                String produto = resultado.getString("produto");
                int quantidade = resultado.getInt("quantidade");

                String key = tipoProduto + " / " + produto;
                estoqueAgrupado.put(key, quantidade);
            }
            return estoqueAgrupado;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}




