import entidade.CentroDistribuicao;

import java.util.Map;
import java.util.Scanner;
import dados.Conexoes;
import dados.CentroDAO;

import logicaCadastro.logicaCentro.LogicaCentro;

import java.sql.Connection;


public class ProgramaCentro {

    public static void main(String[] args) {

        try (Connection conexao = Conexoes.conexao()) {
            CentroDAO centroDAO = new CentroDAO(conexao);
            LogicaCentro logicaCentro = new LogicaCentro(centroDAO);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Menu:");
                System.out.println("1. Cadastrar novo centro de distribuição");
                System.out.println("2. Listar todos os centros de distribuição");
                System.out.println("3. Verificar estoque de um centro");
                System.out.println("4. Sair");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                if (opcao == 1) {
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Endereço: ");
                    String endereco = scanner.nextLine();
                    System.out.print("CEP: ");
                    String cep = scanner.nextLine();

                    CentroDistribuicao centro = new CentroDistribuicao(nome, endereco, cep);
                    try {
                        logicaCentro.salvar(centro);
                        System.out.println("Centro de distribuição cadastrado com sucesso!");
                    } catch (Exception e) {
                        System.out.println("Erro ao cadastrar centro de distribuição: " + e.getMessage());
                    }
                } else if (opcao == 2) {
                    System.out.println("Listando todos os centros de distribuição:");
                    logicaCentro.imprimir();
                } else if (opcao == 3) {
                    System.out.print("Digite o código do centro para verificar o estoque: ");
                    String codigoCentro = scanner.nextLine();

                    Map<String, Integer> estoqueAgrupado = centroDAO.listarEstoqueAgrupado(codigoCentro);

                    if (estoqueAgrupado.isEmpty()) {
                        System.out.println("Nenhum item encontrado no estoque deste centro.");
                    } else {
                        System.out.println("Estoque do centro " + codigoCentro + ":");

                        for (Map.Entry<String, Integer> entry : estoqueAgrupado.entrySet()) {
                            String key = entry.getKey();
                            int quantidade = entry.getValue();

                            System.out.println(key + " / Em estoque: " + quantidade);
                        }
                    }
                } else if (opcao == 4) {
                    System.out.println("Saindo...");
                    break;
                } else {
                    System.out.println("Opção inválida!");
                }
            }

        } catch (Exception e) {
            System.out.println("Erro na conexão com o banco de dados: " + e.getMessage());
        }
    }
}
