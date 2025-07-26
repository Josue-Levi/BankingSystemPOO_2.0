package clients;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import validation.ValidadorCNPJ;

public class CadastroPessoaJuridica {
        public static PessoaJuridica CadastrarPessoaJuridica(Scanner scanner) {
                System.out.println("===== CADASTRO DE PESSOA JURIDICA =====");

                //entrada dos dados pessoais
                System.out.print("Nome do Responsável: ");
                String nome = scanner.nextLine();

                System.out.print("Data de Nascimento do Responsável (dd/mm/aaaa): ");
                String nascimento = scanner.nextLine();

                System.out.print("CPF do Responsável: ");
                String cpf = scanner.nextLine();

                System.out.print("Email do Responsável: ");
                String email = scanner.nextLine();

                System.out.print("Senha do Responsável: ");
                String senha = scanner.nextLine();

                System.out.print("Telefone do Responsável: ");
                String telefone = scanner.nextLine();

                // entrada dos dados da empresa
                System.out.println("===== CADASTRO DE INFORMAÇÕES DA EMPRESA =====");

                System.out.print("Razão Social: ");
                String razaosocial = scanner.nextLine();

                String CNPJ;
                boolean CNPJvalido = false;
                do {
                        System.out.print("Digite o CNPJ (apenas números): ");
                        CNPJ = scanner.nextLine();
                        CNPJvalido = ValidadorCNPJ.validarCNPJ(CNPJ);
                } while (!CNPJvalido);


                // criação do objeto com dados pessoais
                PessoaJuridica pessoa = new PessoaJuridica(nome, nascimento, cpf, email, senha, telefone, CNPJ, razaosocial);

                // entrada dos dados de endereço
                System.out.println("\n===== CADASTRO DE LOCALIZAÇÃO DA EMPRESA =====");

                System.out.print("Estado: ");
                String estado = scanner.nextLine();

                System.out.print("Cidade: ");
                String cidade = scanner.nextLine();

                System.out.print("Bairro: ");
                String bairro = scanner.nextLine();

                System.out.print("Rua: ");
                String rua = scanner.nextLine();

                System.out.print("Número: ");
                int numero = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Complemento: ");
                String complemento = scanner.nextLine();

                System.out.print("CEP: ");
                String cep = scanner.nextLine();

                // criação do objeto com dados da localização da empresa
                pessoa.setEndereco(estado, cidade, bairro, rua, numero, complemento, cep);

                // exibir dados cadastrado pela pessoa juridica
                pessoa.ExibirDadosPessoaJuridica();

                // Salva em .Json
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                List<PessoaJuridica> pessoas = new ArrayList<>();

                // Tenta carregar os dados existentes do arquivo
                try (FileReader reader = new FileReader("src/clients/CadastrosJuridico.json")) {
                        Type listType = new TypeToken<List<PessoaJuridica>>() {}.getType();
                        List<PessoaJuridica> existentes = gson.fromJson(reader, listType);
                        if (existentes != null) {
                                pessoas.addAll(existentes);
                        }
                } catch (IOException e) {
                        // Arquivo não existe ou está vazio, é a primeira vez que salvamos
                        System.out.println("Arquivo CadastrosJuridico.json não encontrado ou vazio. Será criado um novo.");
                } catch (com.google.gson.JsonSyntaxException e) {
                        System.err.println("Erro na sintaxe JSON do arquivo CadastrosJuridico.json. Criando um novo. Erro: " + e.getMessage());
                }

                // Adiciona a nova pessoa à lista
                pessoas.add(pessoa);

                // Salva a lista completa de volta no arquivo
                try (FileWriter writer = new FileWriter("src/clients/CadastrosJuridico.json")) {
                        gson.toJson(pessoas, writer);
                        System.out.println("Dados de Pessoa Jurídica salvos com sucesso!");
                } catch (IOException e) {
                        System.out.println("Erro ao salvar os dados no arquivo JSON: " + e.getMessage());
                        e.printStackTrace();
                }
                return pessoa;
        }
}