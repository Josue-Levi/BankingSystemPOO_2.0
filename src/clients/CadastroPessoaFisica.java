package clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken; 
import validation.ValidadorCPF;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type; 
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CadastroPessoaFisica {
        public static PessoaFisica CadastrarPessoaFisica(Scanner scanner){

                System.out.println("===== CADASTRO DE PESSOA FÍSICA =====");
                // entrada dos dados pessoais
                System.out.print("Nome: ");
                String nome = scanner.nextLine();

                System.out.print("Data de nascimento (dd/mm/aaaa): ");
                String nascimento = scanner.nextLine();

                String CPF;
                boolean CPFvalido = false;
                do {
                        System.out.print("CPF (apenas números): ");
                        CPF = scanner.nextLine();
                        CPFvalido = ValidadorCPF.validarCPF(CPF);
                } while (!CPFvalido);

                System.out.print("Email: ");
                String email = scanner.nextLine();

                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                System.out.print("Telefone: ");
                String telefone = scanner.nextLine();

                // criação do objeto com dados pessoais
                PessoaFisica pessoa = new PessoaFisica(nome, nascimento, CPF, email, senha, telefone);

                // entrada dos dados de endereço
                System.out.println("\n===== CADASTRO DE ENDEREÇO =====");

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

                // criação do objeto com dados da residencia
                pessoa.setEndereco(estado, cidade, bairro, rua, numero, complemento, cep);

                // exibir dados inseridos pela pessoa fisica
                pessoa.ExibirDadosPessoaFisica();

                // Salva em .Json
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                List<PessoaFisica> pessoas = new ArrayList<>();

                // Tenta carregar os dados existentes do arquivo
                try (FileReader reader = new FileReader("src/clients/CadastrosFisica.json")) {
                        Type listType = new TypeToken<List<PessoaFisica>>() {}.getType();
                        List<PessoaFisica> existentes = gson.fromJson(reader, listType);
                        if (existentes != null) {
                                pessoas.addAll(existentes);
                        }
                } catch (IOException e) {
                        // Arquivo não existe ou está vazio, é a primeira vez que salvamos
                        System.out.println("Arquivo CadastrosFisica.json não encontrado ou vazio. Será criado um novo.");
                } catch (com.google.gson.JsonSyntaxException e) {
                        System.err.println("Erro na sintaxe JSON do arquivo CadastrosFisica.json. Criando um novo. Erro: " + e.getMessage());
                        // Opcional: fazer backup do arquivo corrompido
                }

                // Adiciona a nova pessoa à lista
                pessoas.add(pessoa);

                // Salva a lista completa de volta no arquivo
                try (FileWriter writer = new FileWriter("src/clients/CadastrosFisica.json")) {
                        gson.toJson(pessoas, writer);
                        System.out.println("Dados de Pessoa Física salvos com sucesso!");
                } catch (IOException e) {
                        System.out.println("Erro ao salvar os dados no arquivo JSON: " + e.getMessage());
                        e.printStackTrace();
                }
                return pessoa;
        }

        
}