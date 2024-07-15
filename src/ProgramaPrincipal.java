import dados.AbrigoDAO;
import dados.CentroDAO;
import dados.Conexoes;
import entidade.Abrigo;
import entidade.CentroDistribuicao;
import estoqueCSV.EstoqueAbrigoCSVImporter;
import estoqueCSV.EstoqueCentroCSVImporter;
import logicaCadastro.logicaAbrigo.LogicaAbrigo;
import logicaCadastro.logicaCentro.LogicaCentro;
import estoqueCSV.Pedido;

import java.sql.Connection;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ProgramaPrincipal {
    public static void main(String[] args) {
        try (Connection conexao = Conexoes.conexao()) {
            AbrigoDAO abrigoDAO = new AbrigoDAO(conexao);
            LogicaAbrigo logicaAbrigo = new LogicaAbrigo(abrigoDAO);
            CentroDAO centroDAO = new CentroDAO(conexao);
            LogicaCentro logicaCentro = new LogicaCentro(centroDAO);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Menu Principal:");
                System.out.println("1. Ajustes Centro");
                System.out.println("2. Ajustes Abrigo");
                System.out.println("3. Receber Pedido");
                System.out.println("4. Alimentar estoque dos Abrigos");
                System.out.println("5. Finalizar");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer

                if (opcao == 1) {
                    ajustesCentro(scanner, logicaCentro, centroDAO);
                } else if (opcao == 2) {
                    ajustesAbrigo(scanner, logicaAbrigo, abrigoDAO);
                } else if (opcao == 3) {
                    receberPedido(scanner);
                } else if (opcao == 4) {
                    alimentarEstoqueCentro(scanner);
                } else if (opcao == 5) {
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

    private static void ajustesCentro(Scanner scanner, LogicaCentro logicaCentro, CentroDAO centroDAO) {
        while (true) {
            System.out.println("Ajustes Centro:");
            System.out.println("1. Cadastrar novo centro de distribuição");
            System.out.println("2. Listar todos os centros de distribuição");
            System.out.println("3. Verificar estoque de um centro");
            System.out.println("4. Voltar");
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
                break;
            } else {
                System.out.println("Opção inválida!");
            }
        }
    }

    private static void ajustesAbrigo(Scanner scanner, LogicaAbrigo logicaAbrigo, AbrigoDAO abrigoDAO) {
        while (true) {
            System.out.println("Ajustes Abrigo:");
            System.out.println("1. Cadastrar novo abrigo");
            System.out.println("2. Listar todos os abrigos");
            System.out.println("3. Atualizar abrigo");
            System.out.println("4. Deletar abrigo");
            System.out.println("5. Verificar estoque do abrigo");
            System.out.println("6. Voltar");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 1) {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("Endereço: ");
                String endereco = scanner.nextLine();
                System.out.print("Responsavel: ");
                String responsavel = scanner.nextLine();
                System.out.print("Telefone: ");
                String telefone = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();

                Abrigo abrigo = new Abrigo(nome, endereco, responsavel, telefone, email);
                try {
                    logicaAbrigo.salvar(abrigo);
                    System.out.println("Abrigo cadastrado com sucesso!");
                } catch (Exception e) {
                    System.out.println("Erro ao cadastrar abrigo: " + e.getMessage());
                }
            } else if (opcao == 2) {
                System.out.println("Listando todos os abrigos:");
                logicaAbrigo.imprimir();
            } else if (opcao == 3) {
                System.out.print("Digite o código do abrigo a ser atualizado: ");
                String codigoStr = scanner.nextLine();
                UUID codigo = UUID.fromString(codigoStr);
                Abrigo abrigoExistente = logicaAbrigo.buscar(codigo);

                if (abrigoExistente != null) {
                    System.out.println("Deixe em branco para manter o valor atual.");
                    System.out.print("Nome (" + abrigoExistente.getNome() + "): ");
                    String nome = scanner.nextLine();
                    System.out.print("Endereço (" + abrigoExistente.getEndereco() + "): ");
                    String endereco = scanner.nextLine();
                    System.out.print("Responsavel (" + abrigoExistente.getResponsavel() + "): ");
                    String responsavel = scanner.nextLine();
                    System.out.print("Telefone (" + abrigoExistente.getTelefone() + "): ");
                    String telefone = scanner.nextLine();
                    System.out.print("Email (" + abrigoExistente.getEmail() + "): ");
                    String email = scanner.nextLine();

                    if (!nome.isEmpty()) abrigoExistente.setNome(nome);
                    if (!endereco.isEmpty()) abrigoExistente.setEndereco(endereco);
                    if (!responsavel.isEmpty()) abrigoExistente.setResponsavel(responsavel);
                    if (!telefone.isEmpty()) abrigoExistente.setTelefone(telefone);
                    if (!email.isEmpty()) abrigoExistente.setEmail(email);

                    try {
                        logicaAbrigo.atualizar(abrigoExistente);
                        System.out.println("Abrigo atualizado com sucesso!");
                    } catch (Exception e) {
                        System.out.println("Erro ao atualizar abrigo: " + e.getMessage());
                    }
                } else {
                    System.out.println("Abrigo não encontrado!");
                }
            } else if (opcao == 4) {
                System.out.print("Digite o código do abrigo a ser deletado: ");
                String codigoStr = scanner.nextLine();
                UUID codigo = UUID.fromString(codigoStr);

                try {
                    logicaAbrigo.deletar(codigo);
                    System.out.println("Abrigo deletado com sucesso!");
                } catch (Exception e) {
                    System.out.println("Erro ao deletar abrigo: " + e.getMessage());
                }
            } else if (opcao == 5) {
                System.out.print("Digite o código do abrigo para listar o estoque: ");
                String codigoAbrigo = scanner.nextLine();

                Map<String, Integer> estoqueAgrupado = abrigoDAO.listarEstoqueAgrupado(codigoAbrigo);

                if (estoqueAgrupado.isEmpty()) {
                    System.out.println("Nenhum item encontrado no estoque deste abrigo.");
                } else {
                    System.out.println("Estoque do abrigo " + codigoAbrigo + ":");
                    for (Map.Entry<String, Integer> entry : estoqueAgrupado.entrySet()) {
                        String key = entry.getKey();
                        int quantidade = entry.getValue();

                        System.out.println(key + " / Em estoque: " + quantidade);
                    }
                }
            } else if (opcao == 6) {
                break;
            } else {
                System.out.println("Opção inválida!");
            }
        }
    }

    private static void receberPedido(Scanner scanner) {
        System.out.println("Escreva o caminho do pedido:");
        String csvFilePath = scanner.nextLine();    //C:/Users/gustt/Documents/pedido.csv
        Pedido.processarPedidos(csvFilePath);
    }

    private static void alimentarEstoqueCentro(Scanner scanner) {
        System.out.println("Escreva o caminho do arquivo CSV para alimentar o estoque dos centros:");
        String csvFilePath = scanner.nextLine(); //C:/Users/gustt/Documents/entrada.csv
        EstoqueCentroCSVImporter.importarCSV(csvFilePath);
        System.out.println("Estoque dos centros atualizado com sucesso!");
    }

}


