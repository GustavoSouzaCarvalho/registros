package estoqueCSV;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dados.Conexoes;
import entidade.EstoqueCentro;
import produtos.TamanhoRoupa;
import produtos.TipoAlimento;
import produtos.TipoHigiene;
import produtos.TipoProduto;

public class EstoqueCentroCSVImporter {
    private static final int LIMITE_OCORRENCIAS_TIPO_PRODUTO = 1000;
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

    public static void importarCSV(String csvFilePath) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFilePath));
             Connection conexao = Conexoes.conexao()) {

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

                Date validade = null;
                if (values.length > 4 && !values[4].trim().isEmpty()) {
                    try {
                        validade = dateFormat.parse(values[4].trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                EstoqueCentro estoqueCentro = new EstoqueCentro(codigo, tipoProduto, especificacaoProduto, quantidade, validade);
                verificarEInserirCentro(conexao, codigo);  // Verificar e inserir o centro se necessário
                inserirEstoqueCentro(conexao, estoqueCentro);
            }
        } catch (IOException | CsvValidationException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void verificarEInserirCentro(Connection conexao, String codigo) throws SQLException {
        String selectSQL = "SELECT codigo FROM centro WHERE codigo = ?";
        try (PreparedStatement selectStmt = conexao.prepareStatement(selectSQL)) {
            selectStmt.setString(1, codigo);
            ResultSet rs = selectStmt.executeQuery();
            if (!rs.next()) {
                String insertSQL = "INSERT INTO centro (codigo, nome, endereco, cep) VALUES (?, 'Nome Padrão', 'Endereço Padrão', '00000000')";
                try (PreparedStatement insertStmt = conexao.prepareStatement(insertSQL)) {
                    insertStmt.setString(1, codigo);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private static void inserirEstoqueCentro(Connection conexao, EstoqueCentro estoqueCentro) throws SQLException {
        if (atingiuLimiteOcorrencias(estoqueCentro.getCodigo(), estoqueCentro.getTipoProduto())) {
            System.out.println("Limite de ocorrências para este tipo de produto atingido para o centro " + estoqueCentro.getCodigo());
            return;
        }
        String sql = "INSERT INTO estoqueCentro (centro, id_estoqueCentro, tipoProduto, quantidade, produto, validade) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, estoqueCentro.getCodigo());
            stmt.setString(2, String.valueOf(estoqueCentro.getId_estoqueCentro()));
            stmt.setString(3, estoqueCentro.getTipoProduto().name());
            stmt.setInt(4, estoqueCentro.getQuantidade());

            String produto = null;
            switch (estoqueCentro.getTipoProduto()) {
                case HIGIENE:
                    produto = estoqueCentro.getTipoHigiene().name();
                    break;
                case ALIMENTO:
                    produto = estoqueCentro.getTipoAlimento().name();
                    break;
                case ROUPA:
                    produto = estoqueCentro.getTamanhoRoupa().name();
                    break;
            }

            stmt.setString(5, produto);
            stmt.setDate(6, estoqueCentro.getValidade() != null ? new java.sql.Date(estoqueCentro.getValidade().getTime()) : null);

            stmt.executeUpdate();
        }
    }
    private static int contarOcorrenciasTipoProduto(Connection conexao, String centroCodigo, TipoProduto tipoProduto) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM estoqueCentro WHERE centro = ? AND tipoProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, centroCodigo);
            stmt.setString(2, tipoProduto.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private static boolean atingiuLimiteOcorrencias(String centroCodigo, TipoProduto tipoProduto) throws SQLException {
        try (Connection conexao = Conexoes.conexao()) {
            int ocorrencias = contarOcorrenciasTipoProduto(conexao, centroCodigo, tipoProduto);
            return ocorrencias >= LIMITE_OCORRENCIAS_TIPO_PRODUTO;
        }
    }

    public static void main(String[] args) {
        String csvFilePath = "C:/Users/gustt/Documents/entrada.csv";
        importarCSV(csvFilePath);
        System.out.println("Feito!");
    }
}
