package estoqueCSV;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dados.Conexoes;
import entidade.EstoqueAbrigo;
import entidade.EstoqueCentro;
import produtos.TamanhoRoupa;
import produtos.TipoAlimento;
import produtos.TipoHigiene;
import produtos.TipoProduto;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class EstoqueAbrigoCSVImporter {
    private static final int LIMITE_OCORRENCIAS_TIPO_PRODUTO = 200;
    private static Map<String, TipoProduto> tipoProdutoMap = new HashMap<>();
    private static Map<String, TipoHigiene> tipoHigieneMap = new HashMap<>();
    private static Map<String, TipoAlimento> tipoAlimentoMap = new HashMap<>();
    private static Map<String, TamanhoRoupa> tamanhoRoupaMap = new HashMap<>();

    static {
        for (TipoProduto tipo : TipoProduto.values()) {
            tipoProdutoMap.put(tipo.name(), tipo);
        }
        for (TipoHigiene tipo : TipoHigiene.values()) {
            tipoHigieneMap.put(tipo.name(), tipo);
        }
        for (TipoAlimento tipo : TipoAlimento.values()) {
            tipoAlimentoMap.put(tipo.name(), tipo);
        }
        for (TamanhoRoupa tamanho : TamanhoRoupa.values()) {
            tamanhoRoupaMap.put(tamanho.name(), tamanho);
        }
    }

    public static List<EstoqueAbrigo> lerPedidosCSV(String csvFilePath) {
        List<EstoqueAbrigo> pedidos = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath))) {

            String[] values;
            csvReader.readNext(); // Ignorar cabeçalho

            while ((values = csvReader.readNext()) != null) {
                // Ignorar linhas vazias
                if (values.length == 0 || values[0].trim().isEmpty()) {
                    continue;
                }

                String codigo = values[0].trim();
                TipoProduto tipoProduto = tipoProdutoMap.get(values[1].trim());
                Object especificacaoProduto = null;

                switch (tipoProduto) {
                    case HIGIENE:
                        especificacaoProduto = tipoHigieneMap.get(values[2].trim());
                        break;
                    case ALIMENTO:
                        especificacaoProduto = tipoAlimentoMap.get(values[2].trim());
                        break;
                    case ROUPA:
                        especificacaoProduto = tamanhoRoupaMap.get(values[2].trim());
                        break;
                }

                Integer quantidade = Integer.parseInt(values[3].trim());

                EstoqueAbrigo estoqueAbrigo = new EstoqueAbrigo(codigo, tipoProduto, especificacaoProduto, quantidade);
                pedidos.add(estoqueAbrigo);
                System.out.println("Pedido lido: " + estoqueAbrigo); // Adicionado para depuração
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return pedidos;
    }


    public static void importarCSV(String csvFilePath) {
        List<EstoqueAbrigo> pedidos = lerPedidosCSV(csvFilePath);
        try (Connection conexao = Conexoes.conexao()) {
            for (EstoqueAbrigo pedido : pedidos) {
                verificarEInserirAbrigo(conexao, pedido.getCodigo());
                inserirEstoqueAbrigo(conexao, pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void verificarEInserirAbrigo(Connection conexao, String codigo) throws SQLException {
        String selectSQL = "SELECT codigo FROM abrigo WHERE codigo = ?";
        try (PreparedStatement selectStmt = conexao.prepareStatement(selectSQL)) {
            selectStmt.setString(1, codigo);
            ResultSet rs = selectStmt.executeQuery();
            if (!rs.next()) {
                String insertSQL = "INSERT INTO abrigo (codigo, nome, responsavel, telefone, email) VALUES (?, 'Nome Padrão', 'Responsável Padrão', '00000000000', 'email@exemplo.com')";
                try (PreparedStatement insertStmt = conexao.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, codigo);
                    insertStmt.executeUpdate();
                }
            }
        }
    }
    private static int contarOcorrenciasTipoProduto(Connection conexao, String codigoAbrigo, TipoProduto tipoProduto) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM estoqueAbrigo WHERE abrigo = ? AND tipoProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, codigoAbrigo);
            stmt.setString(2, tipoProduto.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private static boolean atingiuLimiteOcorrencias(String codigoAbrigo, TipoProduto tipoProduto) throws SQLException {
        try (Connection conexao = Conexoes.conexao()) {
            int ocorrencias = contarOcorrenciasTipoProduto(conexao, codigoAbrigo, tipoProduto);
            return ocorrencias >= LIMITE_OCORRENCIAS_TIPO_PRODUTO;
        }
    }
    private static void inserirEstoqueAbrigo(Connection conexao, EstoqueAbrigo estoqueAbrigo) throws SQLException {
        if (atingiuLimiteOcorrencias(estoqueAbrigo.getCodigo(), estoqueAbrigo.getTipoProduto())) {
            System.out.println("Limite de ocorrências para este tipo de produto atingido para o abrigo " + estoqueAbrigo.getCodigo());
            return;
        }
        Timestamp momento = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(momento);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Timestamp prazo = new Timestamp(calendar.getTimeInMillis());

        String sql = "INSERT INTO estoqueAbrigo (abrigo, id_estoqueAbrigo, tipoProduto, quantidade, momento, prazo, produto, validade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, estoqueAbrigo.getCodigo());
            stmt.setString(2, String.valueOf(estoqueAbrigo.getId_estoqueAbrigo()));
            stmt.setString(3, estoqueAbrigo.getTipoProduto().name());
            stmt.setInt(4, estoqueAbrigo.getQuantidade());
            stmt.setTimestamp(5, momento);
            stmt.setTimestamp(6, prazo);

            String produto = null;
            switch (estoqueAbrigo.getTipoProduto()) {
                case HIGIENE:
                    produto = estoqueAbrigo.getTipoHigiene().name();
                    break;
                case ALIMENTO:
                    produto = estoqueAbrigo.getTipoAlimento().name();
                    break;
                case ROUPA:
                    produto = estoqueAbrigo.getTamanhoRoupa().name();
                    break;
            }
            stmt.setString(7, produto);
            stmt.setDate(8, estoqueAbrigo.getValidade() != null ? new java.sql.Date(estoqueAbrigo.getValidade().getTime()) : null);

            stmt.executeUpdate();
        }
    }
}
