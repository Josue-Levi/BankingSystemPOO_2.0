# Sistema Bancário SSJ

## Sobre:
O SSJ é um simulador de um sistema bancário básico em Java, implementando conceitos de Programação Orientada a Objetos (POO). O sistema permite o cadastro de cliente (pessoas física ou jurídica) e a abertura de diferentes tipos de conta (conta corrente ou poupança), além da realização de operações bancárias fundamentais, como saques, depósitos e transferências entre contas.
O foco do projeto é a aplicação e consolidação de conceitos de Programação Orientada a Objetos.

## Como compilar e rodar:
1. Certifique-se de ter o Java Development Kit (JDK) 11 ou superior instalado na sua máquina. 
2. Atente-se à estrutura de pastas. Salve os arquivos em uma mesma pasta e compile-a.
3. Abra o terminal (ou prompt de comando) na pasta onde estão seus arquivos .java e compile-os:
```bash
    javac *.java
```
4. Rodar: Após a compilação, execute a classe Main:
```bash
    java Main
```

## Funcionalidades:
- Cadastro de Clientes: 
    - Pessoa Física: Registro de nome, data de nascimento, CPF, telefone, endereço (estado, cidade, bairro, rua, número, complemento, CEP) e credenciais de login (email e senha).
    - Pessoa Jurídica: Registro de nome do responsável, razão social, CPF do responsável, CNPJ, telefone, endereço e credenciais de login.
- Validação de Documentos: 
    - Validação de CPF e CNPJ durante o cadastro para garantir a integridade dos dados.
- Abertura de Contas: 
    - Conta Corrente: Permite saques, depósitos, extrato, com funcionalidades de cheque especial e controle de taxas de manutenção e transações.
    - Conta Poupança: Permite saques, depósitos, extrato, com aplicação automática de rendimentos mensais.
- Login na Conta: Acesso às contas existentes através de email e senha, listando todas as contas vinculadas a um mesmo login.
- Operações Bancárias:
    - Depósito: Adiciona valor ao saldo da conta.
    - Saque: Retira valor do saldo da conta, considerando o cheque especial para Conta Corrente e aplicando taxas/rendimentos quando aplicável.
    - Extrato: Exibe um resumo detalhado das informações da conta e saldo.
    - Transferência: Permite transferir fundos entre contas diferentes dentro do sistema, validando a disponibilidade de saldo.
- Estrutura POO:
    - Uso de classes abstratas (Cliente, Conta) para definir contratos e atributos comuns.
    - Herança para especializar tipos de clientes (PessoaFisica, PessoaJuridica) e tipos de contas (ContaCorrente, ContaPoupanca).
    - Polimorfismo para tratar diferentes tipos de contas e clientes de forma unificada.
    - Classes utilitárias e de Serviços (ValidadorCPF, ValidadorCNPJ, Transferir) para modularizar lógicas específicas.

## Tecnologias usadas:
- Java
- API java.time: Para manipulação de datas e horas de forma moderna e eficiente.
- java.util.Scanner: Para interação com o usuário via console.
- java.util.ArrayList e java.util.List: Para armazenamento em memória das contas e clientes.
- Google Gson para manipulação de JSON.

## Estrutura do Projeto:
O código está organizado nos seguintes pacotes:
```
/
|___src/
    |---Main.java
    |---clients/
    |   |---CadastroPessoaFisica.java
    |   |---CadastroPessoaJuridica.java
    |   |---CadastrosFisica.json
    |   |---CadastrosJuridico.json
    |   |---PessoaFisica.java
    |   |---PessoaJuridica.java
    |---data/
    |   |---Contas.json
    |---model/
    |   |---Conta.java
    |   |---ContaCorrente.java
    |   |---ContaPoupanca.java
    |---servicos/
    |   |---Transacao.java
    |---util/
    |   |---LocalDateTimeAdapter.java
    |   |---RuntimeTypeAdapterFactory.java
    |---valitadion/
    |   |---ValidadorCNPJ.java
    |   |---ValidadorCPF.java
    |___|---Gerador.java
```


## Como usar: 
1. Criar Conta: Escolha se deseja criar uma conta para Pessoa Física ou Jurídica. Preencha os dados solicitados, incluindo informações pessoais, endereço e dados de login. Em seguida, escolha o tipo de conta (Corrente ou Poupança) e defina o saldo inicial (e limite de cheque especial, se for Conta Corrente).
2. Entrar em Conta Existente: Digite o email e a senha cadastrados. Se houver contas associadas, você poderá selecionar qual deseja acessar.
3. Operações da Conta: Uma vez logado em uma conta, você terá opções para consultar o extrato, realizar depósitos, saques ou transferências para outras contas existentes no sistema.
4. Sair: Use a opção 0 nos menus para sair do menu atual ou do programa principal.

## Lógica de Persistência e Migração:
O sistema utiliza a biblioteca Gson para serializar (Java -> JSON) e desserializar (JSON -> Java) os objetos. Para lidar com a herança (ContaCorrente e ContaPoupanca são tipos de Conta), foi implementado um RuntimeTypeAdapterFactory.

Isso adiciona um campo de "tipo" (ex: "tipoConta": "ContaCorrente") aos arquivos JSON, permitindo que o Gson saiba exatamente qual classe instanciar ao ler os dados. O programa também possui uma rotina de migração que adiciona esses campos automaticamente a arquivos antigos que não os possuam.

## Equipe: 
- Josué Levi Oliveira Lima
- Sergio Henrique Lobato Leal
- Suzane Bianca da Silva Antunes Moura