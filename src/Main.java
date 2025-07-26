import clients.CadastroPessoaFisica;
import clients.CadastroPessoaJuridica;
import clients.PessoaFisica;
import clients.PessoaJuridica;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import services.Transacao;
import util.LocalDateTimeAdapter;
import util.RuntimeTypeAdapterFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static final String FILE_PF = "src/clients/CadastrosFisica.json";
    private static final String FILE_PJ = "src/clients/CadastrosJuridico.json";
    private static final String FILE_CONTAS = "src/data/contas.json";

    private static List<PessoaFisica> clientesPF = Collections.synchronizedList(new ArrayList<>());
    private static List<PessoaJuridica> clientesPJ = Collections.synchronizedList(new ArrayList<>());
    private static List<Conta> contas = Collections.synchronizedList(new ArrayList<>());

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static Gson gson = criarGson();

    private static Gson criarGson() {
        RuntimeTypeAdapterFactory<PessoaFisica> titularAdapterFactory = RuntimeTypeAdapterFactory
                .of(PessoaFisica.class, "titularType")
                .registerSubtype(PessoaFisica.class, "PessoaFisica")
                .registerSubtype(PessoaJuridica.class, "PessoaJuridica");

        RuntimeTypeAdapterFactory<Conta> contaAdapterFactory = RuntimeTypeAdapterFactory
                .of(Conta.class, "tipoConta")
                .registerSubtype(ContaCorrente.class, "ContaCorrente")
                .registerSubtype(ContaPoupanca.class, "ContaPoupanca");

        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapterFactory(contaAdapterFactory)
                .registerTypeAdapterFactory(titularAdapterFactory)
                .create();
    }
    
    public static void main(String[] args) {
        new File("src/clients").mkdirs();
        new File("src/data").mkdirs();
        carregarDados();

        int opcaoPrincipal;
        do {
            exibirMenuPrincipal();
            opcaoPrincipal = escolherOpcao();
            switch (opcaoPrincipal) {
                case 1:
                    criarConta();
                    break;
                case 2:
                    entrarConta();
                    break;
                case 0:
                    System.out.println("Saindo do simulador... Adeus.");
                    salvarDadosAsync();
                    desligarExecutor();
                    break;
                default:
                    System.out.println("Opção inválida. Digite uma das opções listadas.");
                    break;
            }
        } while (opcaoPrincipal != 0);
        scanner.close();
    }

    private static void carregarDados() {
        System.out.println("Carregando dados existentes em paralelo...");

        CompletableFuture<Boolean> futurePF = carregarClientesPFAsync();
        CompletableFuture<Boolean> futurePJ = carregarClientesPJAsync();
        CompletableFuture<Boolean> futureContas = carregarContasAsync();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futurePF, futurePJ, futureContas);

        try {
            allFutures.get();
        } catch (Exception e) {
            System.err.println("Erro ao esperar o carregamento dos dados: " + e.getMessage());
        }
        
        boolean dadosMigrados = false;
        try {
           dadosMigrados = futurePF.get() || futurePJ.get() || futureContas.get();
        } catch (Exception e) {
            System.err.println("Erro ao obter resultado da migração: " + e.getMessage());
        }

        System.out.println("Dados carregados com sucesso.");

        if (dadosMigrados) {
            System.out.println("Migração de dados concluída. Salvando arquivos no novo formato...");
            salvarDadosAsync();
        }
    }

    private static CompletableFuture<Boolean> carregarClientesPFAsync() {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(FILE_PF);
            if (!file.exists() || file.length() == 0) return false;

            boolean migrado = false;
            try (FileReader reader = new FileReader(file)) {
                JsonElement element = JsonParser.parseReader(reader);
                
                if (element.isJsonArray()) {
                    for (JsonElement item : element.getAsJsonArray()) {
                        JsonObject obj = item.getAsJsonObject();
                        if (!obj.has("titularType")) {
                            obj.addProperty("titularType", "PessoaFisica");
                            migrado = true;
                        }
                    }
                }
                
                clientesPF = gson.fromJson(element, new TypeToken<List<PessoaFisica>>() {}.getType());
                if (clientesPF == null) clientesPF = Collections.synchronizedList(new ArrayList<>());
            } catch (Exception e) {
                System.err.println("Erro ao carregar ou migrar clientes PF: " + e.getMessage());
                clientesPF = Collections.synchronizedList(new ArrayList<>());
            }
            System.out.println("-> Clientes PF carregados.");
            return migrado;
        }, executor);
    }

    private static CompletableFuture<Boolean> carregarClientesPJAsync() {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(FILE_PJ);
            if (!file.exists() || file.length() == 0) return false;

            boolean migrado = false;
            try (FileReader reader = new FileReader(file)) {
                JsonElement element = JsonParser.parseReader(reader);

                if (element.isJsonArray()) {
                    for (JsonElement item : element.getAsJsonArray()) {
                        JsonObject obj = item.getAsJsonObject();
                        if (!obj.has("titularType")) {
                            obj.addProperty("titularType", "PessoaJuridica");
                            migrado = true;
                        }
                    }
                }

                clientesPJ = gson.fromJson(element, new TypeToken<List<PessoaJuridica>>() {}.getType());
                if (clientesPJ == null) clientesPJ = Collections.synchronizedList(new ArrayList<>());
            } catch (Exception e) {
                System.err.println("Erro ao carregar ou migrar clientes PJ: " + e.getMessage());
                clientesPJ = Collections.synchronizedList(new ArrayList<>());
            }
            System.out.println("-> Clientes PJ carregados.");
            return migrado;
        }, executor);
    }

    private static CompletableFuture<Boolean> carregarContasAsync() {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(FILE_CONTAS);
            if (!file.exists() || file.length() == 0) return false;

            boolean migrado = false;
            try (FileReader reader = new FileReader(file)) {
                JsonElement element = JsonParser.parseReader(reader);

                if (element.isJsonArray()) {
                    for (JsonElement item : element.getAsJsonArray()) {
                        JsonObject contaObj = item.getAsJsonObject();
                        if (contaObj.has("titular") && contaObj.get("titular").isJsonObject()) {
                            JsonObject titularObj = contaObj.getAsJsonObject("titular");
                            if (!titularObj.has("titularType")) {
                                migrado = true;
                                if (titularObj.has("CNPJ")) {
                                    titularObj.addProperty("titularType", "PessoaJuridica");
                                } else {
                                    titularObj.addProperty("titularType", "PessoaFisica");
                                }
                            }
                        }
                    }
                }

                contas = gson.fromJson(element, new TypeToken<List<Conta>>() {}.getType());
                if (contas == null) contas = Collections.synchronizedList(new ArrayList<>());
            } catch (Exception e) {
                System.err.println("Erro ao carregar ou migrar contas: " + e.getMessage());
                contas = Collections.synchronizedList(new ArrayList<>());
            }
            System.out.println("-> Contas carregadas.");
            return migrado;
        }, executor);
    }
    
    public static void exibirMenuPrincipal() {
        System.out.println("\n===== MENU PRINCIPAL =====");
        System.out.println("[1] Abrir Nova Conta");
        System.out.println("[2] Entrar em Conta Existente");
        System.out.println("[0] Sair");
        System.out.println("Escolha uma opção: ");
    }

    public static void exibirTiposDeConta() {
        System.out.println("\n===== TIPOS DE CONTA =====");
        System.out.println("[1] Conta Corrente");
        System.out.println("[2] Conta Poupança");
    }

    public static void exibirTipoDeCliente() {
        System.out.println("\n===== TIPOS DE CLIENTE =====");
        System.out.println("[1] Pessoa Física");
        System.out.println("[2] Pessoa Jurídica");
    }

    public static int escolherOpcao() {
        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (InputMismatchException exception) {
            System.out.println("Entrada inválida. Por favor, digite um número.\n");
            scanner.nextLine();
            return -1;
        }
    }

    public static void criarConta() {
        Object titularConta = null;
        exibirTipoDeCliente();
        int tipoCliente = escolherOpcao();

        switch (tipoCliente) {
            case 1:
                PessoaFisica pessoaFisica = CadastroPessoaFisica.CadastrarPessoaFisica(scanner);
                if (pessoaFisica != null) {
                    if (clientesPF.stream().noneMatch(p -> p.equals(pessoaFisica))) {
                        clientesPF.add(pessoaFisica);
                    }
                    titularConta = pessoaFisica;
                }
                break;
            case 2:
                PessoaJuridica pessoaJuridica = CadastroPessoaJuridica.CadastrarPessoaJuridica(scanner);
                if (pessoaJuridica != null) {
                      if (clientesPJ.stream().noneMatch(p -> p.equals(pessoaJuridica))) {
                        clientesPJ.add(pessoaJuridica);
                    }
                    titularConta = pessoaJuridica;
                }
                break;
            default:
                System.out.println("Opção inválida para tipo de cliente. Operação cancelada.\n");
                return;
        }

        if (titularConta != null) {
            exibirTiposDeConta();
            int tipoConta = escolherOpcao();
            Conta novaConta = null;

            switch (tipoConta) {
                case 1:
                    double limiteChequeEspecial = 0.0;
                    boolean limiteValido = false;
                    while (!limiteValido) {
                        System.out.print("Digite o limite do cheque especial: R$ ");
                        try {
                            limiteChequeEspecial = scanner.nextDouble();
                            scanner.nextLine();
                            if (limiteChequeEspecial < 0) {
                                System.out.println("O limite do cheque especial não pode ser negativo.");
                            } else {
                                limiteValido = true;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Valor inválido. Por favor, digite um número.");
                            scanner.nextLine();
                        }
                    }
                    novaConta = new ContaCorrente((PessoaFisica) titularConta, 0.0, limiteChequeEspecial);
                    break;
                case 2:
                    novaConta = new ContaPoupanca((PessoaFisica) titularConta, 0.0);
                    break;
                default:
                    System.out.println("Opção inválida para tipo de conta. Operação cancelada.\n");
                    return;
            }

            if (novaConta != null) {
                contas.add(novaConta);
                novaConta.gerarExtrato();
                salvarDadosAsync();
            }
        } else {
            System.out.println("Não foi possível criar a conta. Cliente inválido.\n");
        }
    }

    public static void entrarConta() {
        System.out.println("\n===== ENTRAR EM CONTA EXISTENTE =====");
        System.out.println("===== LOGIN CONTA =====");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Object clienteLogado = verificarLogin(email, senha);

        if (clienteLogado != null) {
            System.out.println("Login bem-sucedido!");
            List<Conta> contasDoCliente = new ArrayList<>();
            for (Conta conta : contas) {
                if (conta.getTitular() != null && conta.getTitular().equals(clienteLogado)) {
                    contasDoCliente.add(conta);
                }
            }

            if (contasDoCliente.isEmpty()) {
                System.out.println("Este cliente não possui contas cadastradas ou vinculadas no sistema.");
                return;
            }

            System.out.println("\nSuas contas:");
            for (int i = 0; i < contasDoCliente.size(); i++) {
                Conta c = contasDoCliente.get(i);
                String tipo = (c instanceof ContaCorrente) ? "Corrente" : "Poupança";
                System.out.printf("[%d] Conta %s: %s (Saldo: R$ %.2f)\n", (i + 1), tipo, c.getNumeroDaConta(), c.getSaldo());
            }

            System.out.print("Selecione o número da conta que deseja acessar: ");
            int escolhaConta = escolherOpcao();

            if (escolhaConta > 0 && escolhaConta <= contasDoCliente.size()) {
                Conta contaSelecionada = contasDoCliente.get(escolhaConta - 1);
                menuOperacoes(contaSelecionada);
            } else {
                System.out.println("Seleção de conta inválida.");
            }
        } else {
            System.out.println("Email ou senha inválidos. Tente novamente.");
        }
    }

    public static Object verificarLogin(String email, String senha) {
        for (PessoaFisica pf : clientesPF) {
            if (pf.getEmail().equalsIgnoreCase(email) && pf.getSenha().equals(senha)) {
                return pf;
            }
        }
        for (PessoaJuridica pj : clientesPJ) {
            if (pj.getEmail().equalsIgnoreCase(email) && pj.getSenha().equals(senha)) {
                return pj;
            }
        }
        return null;
    }

    public static void menuOperacoes(Conta conta) {
        int opcaoOperacao;
        do {
            System.out.println("\n===== OPERAÇÕES DA CONTA " + conta.getNumeroDaConta() + " =====");
            System.out.printf("Saldo Atual: R$ %.2f\n", conta.getSaldo());
            System.out.println("[1] Depositar");
            System.out.println("[2] Sacar");
            System.out.println("[3] Gerar Extrato");
            System.out.println("[4] Transferir para outra conta");
            System.out.println("[0] Voltar ao Menu Principal");
            System.out.println("Escolha uma opção: ");
            opcaoOperacao = escolherOpcao();

            switch (opcaoOperacao) {
                case 1:
                    System.out.print("Digite o valor do depósito: R$ ");
                    try {
                        double valorDeposito = scanner.nextDouble();
                        scanner.nextLine();
                        conta.depositar(valorDeposito);
                        salvarDadosAsync();
                    } catch (InputMismatchException e) {
                        System.out.println("Valor inválido. Por favor, digite um número.");
                        scanner.nextLine();
                    }
                    break;
                case 2:
                    System.out.print("Digite o valor do saque: R$ ");
                      try {
                        double valorSaque = scanner.nextDouble();
                        scanner.nextLine();
                        conta.sacar(valorSaque);
                        salvarDadosAsync();
                    } catch (InputMismatchException e) {
                        System.out.println("Valor inválido. Por favor, digite um número.");
                        scanner.nextLine();
                    }
                    break;
                case 3:
                    conta.gerarExtrato();
                    break;
                case 4:
                    System.out.print("Digite o número da conta de destino: ");
                    String contaDestino = scanner.nextLine();
                    System.out.print("Digite o valor para transferir: R$ ");
                    try {
                        double valorTransferencia = scanner.nextDouble();
                        scanner.nextLine();
                        boolean sucesso = Transacao.transferir(contas, conta.getNumeroDaConta(), contaDestino, valorTransferencia);
                        if (sucesso) salvarDadosAsync();
                    } catch (InputMismatchException e) {
                        System.out.println("Valor inválido. Por favor, digite um número.");
                        scanner.nextLine();
                    }
                    break;
                case 0:
                    System.out.println("Voltando ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida. Digite uma das opções listadas.");
                    break;
            }
        } while (opcaoOperacao != 0);
    }

    private static void salvarListaEmArquivo(List<?> lista, String caminhoArquivo, String nomeLista) {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            gson.toJson(lista, writer);
            System.out.println("-> " + nomeLista + " salvos.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + nomeLista + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static CompletableFuture<Void> salvarDadosAsync() {
        System.out.println("Salvando todos os dados em segundo plano (paralelamente)...");

        CompletableFuture<Void> futurePF = CompletableFuture.runAsync(
            () -> salvarListaEmArquivo(clientesPF, FILE_PF, "Clientes PF"), executor);

        CompletableFuture<Void> futurePJ = CompletableFuture.runAsync(
            () -> salvarListaEmArquivo(clientesPJ, FILE_PJ, "Clientes PJ"), executor);

        CompletableFuture<Void> futureContas = CompletableFuture.runAsync(
            () -> salvarListaEmArquivo(contas, FILE_CONTAS, "Contas"), executor);

        return CompletableFuture.allOf(futurePF, futurePJ, futureContas)
            .whenComplete((res, ex) -> {
                if (ex != null) {
                    System.err.println("Ocorreu um erro geral durante o salvamento paralelo: " + ex.getCause().getMessage());
                } else {
                    System.out.println("Salvamento em segundo plano concluído com sucesso.");
                }
            });
    }

    private static void desligarExecutor() {
        System.out.println("Encerrando threads de serviço...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Executor não pode ser encerrado.");
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Serviços encerrados.");
    }
}