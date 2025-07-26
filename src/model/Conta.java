package model;

import clients.PessoaFisica;
import clients.PessoaJuridica;
import validation.Gerador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


// classe abstrata que serve como modelo para as classes ContaCorrete.java e ContaPoupança.java
public abstract class Conta {
    // realiza a formatação da data
    private transient static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static DateTimeFormatter getFormatter() {
        return FORMATTER;
    }

    // atributos protegidos, visivel para as classes filhas.
    protected PessoaFisica titular;
    protected String numeroDaConta;
    protected String agencia;
    protected String codigoBanco;
    protected String codigoOperacao;
    protected double saldo;
    protected LocalDateTime dataCriacao;

    // construtor chamado por classes filhas para criar nova conta.
    public Conta(PessoaFisica titular, double saldoInicial) {
        this.titular = titular;
        this.saldo = saldoInicial;
        this.dataCriacao = LocalDateTime.now();

        // utiliza o gerador para definir os dados da conta.
        Gerador gerador = new Gerador();
        this.numeroDaConta = gerador.getNumeroConta();
        this.agencia = gerador.getAgencia();
        this.codigoBanco = gerador.getCodigoBanco();
        this.codigoOperacao = gerador.getCodigoOperar();

        // exibição para o usuario.
        System.out.println("\n===== CONTA CRIADA COM SUCESSO! =====");
        if (titular instanceof PessoaJuridica) {
            System.out.println("Titular (Responsável): " + ((PessoaJuridica) titular).getNomePessoa());
            System.out.println("Razão Social: " + ((PessoaJuridica) titular).getRazaoSocial());
        } else if (titular instanceof PessoaFisica) {
            System.out.println("Titular: " + titular.getNomePessoa());
        }
        System.out.println("Número da Conta: " + this.numeroDaConta);
        System.out.println("Agência: " + this.agencia);
        System.out.println("Banco: " + this.codigoBanco);
        System.out.println("Operação: " + this.codigoOperacao);
        System.out.printf("Saldo Inicial: R$ %.2f\n", this.saldo);
        System.out.println("---------------------------------");
    }

    // Getters são metodos utilizado para obter valores dos atributos
    public PessoaFisica getTitular() {
        return titular;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getNumeroDaConta() {
        return numeroDaConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public String getCodigoOperacao() {
        return codigoOperacao;
    }

    // Setters são metodos para alterar os valores dos atributos
    public void setTitular(PessoaFisica titular) {
        this.titular = titular;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    // adiciona valor ao saldo, caso o valor seja positivo
    public void depositar(double valor) {
        if (valor > 0) {
            LocalDateTime agora = LocalDateTime.now();
            String dataHoraFormatada = agora.format(FORMATTER);
            this.saldo += valor;
            System.out.printf("Depósito de R$ %.2f realizado. Novo saldo: R$ %.2f.\n", valor, this.saldo);
            System.out.printf("Data e hora do depósito: %s.\n", dataHoraFormatada);
        } else {
            System.out.println("Valor de depósito inválido!\n");
        }
    }
    
    // retira valor do saldo, se caso for um valor valido e caso houver saldo.
    public void sacar(double valor) {
        if (valor > 0 && this.saldo >= valor) {
            LocalDateTime agora = LocalDateTime.now();
            String dataHoraFormatada = agora.format(FORMATTER);
            this.saldo -= valor;
            System.out.printf("Saque de R$ %.2f realizado. Novo saldo: R$ %.2f.\n", valor, this.saldo);
            System.out.printf("Data e hora do saque: %s.\n", dataHoraFormatada);
        } else {
            System.out.println("Valor de saque inválido ou saldo insuficiente!\n");
        }
    }

    // metodo abstrato utilizado nas classes filhas para implementarem a propria logica de gerar extrato.
    public abstract void gerarExtrato();
}
