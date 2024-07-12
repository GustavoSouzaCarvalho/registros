package dados;

import entidade.CentroDistribuicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CentroDAO {
    private Connection conexao;

    public CentroDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(CentroDistribuicao centro){
        try {
            String comandoSQL = """
                    insert into centro (codigo, nome, endereco, cep)
                    values(?, ?, ?, ?)
                    
                    """;
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, String.valueOf(centro.getCodigo()));
            comando.setString(2, centro.getNome());
            comando.setString(3, centro.getEndereco());
            comando.setString(4, centro.getCEP());

            comando.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void atualizar(CentroDistribuicao centro) {
        try {
            StringBuilder comandoSQL = new StringBuilder("update centro set ");
            List<Object> parametros = new ArrayList<>();

            if (centro.getNome() != null) {
                comandoSQL.append("nome = ?, ");
                parametros.add(centro.getNome());
            }
            if (centro.getEndereco() != null) {
                comandoSQL.append("endereco = ?, ");
                parametros.add(centro.getEndereco());
            }
            if (centro.getCEP() != null) {
                comandoSQL.append("cep = ?, ");
                parametros.add(centro.getCEP());
            }


            // Remove a última vírgula e espaço
            comandoSQL.setLength(comandoSQL.length() - 2);
            comandoSQL.append(" where codigo = ?");
            parametros.add(centro.getCodigo());

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
            String comandoSQL = "delete from centro where codigo = ?";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, codigo);
            comando.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<CentroDistribuicao> listar(){
        try {
            String comandoSQL = "select * from centro";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);

            ResultSet resultado = comando.executeQuery();

            List<CentroDistribuicao> lista = new ArrayList<>();

            while(resultado.next()) {
                String codigo = resultado.getString("codigo");
                String nome = resultado.getString("nome");
                String endereco = resultado.getString("endereco");
                String cep = resultado.getString("cep");



                CentroDistribuicao centro = new CentroDistribuicao();

                centro.setCodigo(UUID.fromString(codigo));
                centro.setNome(nome);
                centro.setEndereco(endereco);
                centro.setCEP(cep);


                lista.add(centro);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean verificarLimiteTipoProduto(String tipoProduto, String codigoCentro) {
        try {
            String comandoSQL = "SELECT COUNT(*) AS total FROM estoqueCentro WHERE tipoProduto = ? AND centro = ?";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, tipoProduto);
            comando.setString(2, codigoCentro);

            ResultSet resultado = comando.executeQuery();
            if (resultado.next()) {
                int total = resultado.getInt("total");
                return total >= 1000; // Verifica se já atingiu o limite no centro
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar limite de tipo de produto por centro: " + e.getMessage());
        }
    }
    public Map<String, Integer> listarEstoqueAgrupado(String codigoCentro) {
        try {
            String comandoSQL = "SELECT tipoProduto, produto, SUM(quantidade) as quantidade FROM estoqueCentro WHERE centro = ? GROUP BY tipoProduto, produto";
            PreparedStatement comando = conexao.prepareStatement(comandoSQL);
            comando.setString(1, codigoCentro);

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

