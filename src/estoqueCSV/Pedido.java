package estoqueCSV;

import dados.Conexoes;
import entidade.EstoqueAbrigo;

import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Pedido {
    public static void processarPedidos(String csvFilePath) {
        List<EstoqueAbrigo> pedidos = EstoqueAbrigoCSVImporter.lerPedidosCSV(csvFilePath);

        try (Connection conexao = Conexoes.conexao()) {
            for (EstoqueAbrigo pedido : pedidos) {
                System.out.println("Processando pedido: " + pedido);
                if (aprovarPedido(pedido)) {
                    System.out.println("Pedido aprovado: " + pedido);
                    atualizarEstoque(pedido, conexao);
                } else {
                    System.out.println("Pedido rejeitado: " + pedido);
                    registrarRejeicao(pedido, conexao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean aprovarPedido(EstoqueAbrigo pedido) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Deseja aprovar o pedido? (s/n)");
        String resposta = scanner.nextLine();
        return resposta.equalsIgnoreCase("s");
    }

    private static void atualizarEstoque(EstoqueAbrigo pedido, Connection conexao) throws SQLException {
        int quantidadeRestante = pedido.getQuantidade();
        while (quantidadeRestante > 0) {
            try (PreparedStatement stmt = conexao.prepareStatement(
                    "SELECT centro, id_estoqueCentro, quantidade, validade FROM estoqueCentro WHERE tipoProduto = ? AND produto = ? ORDER BY quantidade DESC")) {
                stmt.setString(1, pedido.getTipoProduto().name());
                stmt.setString(2, getProdutoName(pedido));
                ResultSet rs = stmt.executeQuery();
                while (rs.next() && quantidadeRestante > 0) {
                    String centro = rs.getString("centro");
                    String idEstoqueCentro = rs.getString("id_estoqueCentro");
                    int quantidadeEstoque = rs.getInt("quantidade");
                    Date validade = rs.getDate("validade");
                    if (quantidadeEstoque >= quantidadeRestante) {
                        atualizarQuantidadeEstoqueCentro(conexao, idEstoqueCentro, quantidadeEstoque - quantidadeRestante);
                        inserirEstoqueAbrigo(conexao, pedido, quantidadeRestante, validade);
                        quantidadeRestante = 0;
                    } else {
                        atualizarQuantidadeEstoqueCentro(conexao, idEstoqueCentro, 0);
                        inserirEstoqueAbrigo(conexao, pedido, quantidadeEstoque, validade);
                        quantidadeRestante -= quantidadeEstoque;
                    }
                }
            }
        }
    }

    private static void atualizarQuantidadeEstoqueCentro(Connection conexao, String idEstoqueCentro, int novaQuantidade) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(
                "UPDATE estoqueCentro SET quantidade = ? WHERE id_estoqueCentro = ?")) {
            stmt.setInt(1, novaQuantidade);
            stmt.setString(2, idEstoqueCentro);
            stmt.executeUpdate();
        }
    }

    private static void inserirEstoqueAbrigo(Connection conexao, EstoqueAbrigo pedido, int quantidade, Date validade) throws SQLException {
        Timestamp momento = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(momento);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Timestamp prazo = new Timestamp(calendar.getTimeInMillis());

        try (PreparedStatement stmt = conexao.prepareStatement(
                "INSERT INTO estoqueAbrigo (abrigo, id_estoqueAbrigo, tipoProduto, produto, quantidade, momento, prazo, validade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, pedido.getCodigo());
            stmt.setString(2, UUID.randomUUID().toString());
            stmt.setString(3, pedido.getTipoProduto().name());
            stmt.setString(4, getProdutoName(pedido));
            stmt.setInt(5, quantidade);
            stmt.setTimestamp(6, momento);
            stmt.setTimestamp(7, prazo);
            stmt.setDate(8, validade != null ? new java.sql.Date(validade.getTime()) : null);
            stmt.executeUpdate();
        }
    }

    private static void registrarRejeicao(EstoqueAbrigo pedido, Connection conexao) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Por favor, insira o motivo da rejeição:");
        String motivo = scanner.nextLine();

        Timestamp momento = new Timestamp(System.currentTimeMillis());

        try (PreparedStatement stmt = conexao.prepareStatement(
                "INSERT INTO rejeicao (abrigo, id_rejeicao, tipoProduto, produto, quantidade, momento, motivo) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, pedido.getCodigo());
            stmt.setString(2, UUID.randomUUID().toString());
            stmt.setString(3, pedido.getTipoProduto().name());
            stmt.setString(4, getProdutoName(pedido));
            stmt.setInt(5, pedido.getQuantidade());
            stmt.setTimestamp(6, momento);
            stmt.setString(7, motivo);
            stmt.executeUpdate();
        }
    }

    private static String getProdutoName(EstoqueAbrigo pedido) {
        switch (pedido.getTipoProduto()) {
            case HIGIENE:
                return pedido.getTipoHigiene().name();
            case ALIMENTO:
                return pedido.getTipoAlimento().name();
            case ROUPA:
                return pedido.getTamanhoRoupa().name();
            default:
                throw new IllegalArgumentException("Tipo de produto inválido");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Escreva o caminho do pedido");
        String csvFilePath =  sc.nextLine();    //C:/Users/gustt/Documents/pedido.csv
        processarPedidos(csvFilePath);
    }
}