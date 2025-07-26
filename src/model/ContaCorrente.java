package model;

import clients.PessoaFisica;
import clients.PessoaJuridica;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class ContaCorrente extends Conta {

    // atributos 
    private final double TAXA_MANUTENCAO_MENSAL = 10.00;
    private final double TAXA_POR_TRANSACAO_EXTRA = 2.50;
    private final int TRANSACOES_GRATUITAS_MES = 20;
    private double limiteChequeEspecial;
    private int contadorTransacoesMes = 0;
    // variaveis para controlar a cobrança da taxa de manutenção
    private int ultimoMesCobrancaTaxaManutencao;
    private int ultimoAnoCobrancaTaxaManutencao;
    private int diaAniversarioCobranca;

    // construtor
    public ContaCorrente(PessoaFisica titular, double saldoInicial, double limiteChequeEspecial) {
        super(titular, saldoInicial);
        this.limiteChequeEspecial = limiteChequeEspecial;
        this.diaAniversarioCobranca = this.dataCriacao.getDayOfMonth();
        this.ultimoMesCobrancaTaxaManutencao = this.dataCriacao.getMonthValue();
        this.ultimoAnoCobrancaTaxaManutencao = this.dataCriacao.getYear();
        System.out.println("Tipo de Conta: Corrente");
        System.out.printf("Limite de Cheque Especial: R$ %.2f\n", this.limiteChequeEspecial);
    }
    
    // getters para acessar aos atributos
    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    //sobrescrição método de saque
    @Override
    public void sacar(double valor){
        cobrarTaxaManutencaoMensal();
        if(valor <= 0){
            System.out.println("Valor de saque inválido!\n");
        } else {
            if(TRANSACOES_GRATUITAS_MES <= contadorTransacoesMes){
                this.saldo -= TAXA_POR_TRANSACAO_EXTRA;
            }
            if((this.saldo + this.limiteChequeEspecial >= valor)){
                if(this.saldo >= valor){
                    this.saldo -= valor;
                    System.out.printf("Saldo de R$ %.2f realizado na conta %s. Novo saldo: R$ %.2f\n", valor, getNumeroDaConta(), this.saldo);
                    this.contadorTransacoesMes += 1;
                } else {
                    double valorUsadoDoLimite = valor - this.saldo;
                    this.saldo = 0;
                    this.limiteChequeEspecial -= valorUsadoDoLimite;
                    System.out.printf("Saque de R$ %.2f realizado (parte do limite de cheque especial). Saldo atual: R$ %.2f. Limite restante: R$ %.2f.\n", valor, this.saldo, this.limiteChequeEspecial);
                    this.contadorTransacoesMes += 1;
                }
                if(TRANSACOES_GRATUITAS_MES <= contadorTransacoesMes){
                    System.out.println("Você possui 0 transações gratuítas restantes.\n");
                } else {
                    System.out.printf("Você possui %d transações gratuítas restantes.\n", TRANSACOES_GRATUITAS_MES - contadorTransacoesMes);
                }
            } else {
                System.out.println("Saldo e limite do cheque especial insuficientes. Não é possível realizar o saque.\n");
            }
        }    
    }

    //sobrescrição método de depósito
    @Override
    public void depositar(double valor){
        super.depositar(valor);
        cobrarTaxaManutencaoMensal();
    }
    
    // sobrescrição do metodo de gerar extrato
    @Override
    public void gerarExtrato() {
        System.out.println("\n===== EXTRATO CONTA CORRENTE ===== \n");
        System.out.printf("Número da Conta: %s\n", this.numeroDaConta);
        System.out.printf("Agência: %s\n", this.agencia);
        System.out.printf("Banco: %s\n", this.codigoBanco);
        System.out.printf("Operação: %s\n", this.codigoOperacao);

        if (this.titular instanceof PessoaJuridica) {
            PessoaJuridica pj = (PessoaJuridica) this.titular;
            System.out.printf("Razão Social: %s (CNPJ %s)\n", pj.getRazaoSocial(), pj.getCNPJ());
            System.out.printf("Responsável: %s\n", pj.getNomePessoa());
        } else if (this.titular instanceof PessoaFisica) {
            System.out.printf("Titular: %s (CPF %s)\n", this.titular.getNomePessoa(), this.titular.getCPF());
        }

        System.out.printf("Saldo: R$ %.2f\n", this.saldo);
        System.out.printf("Limite de Cheque Especial: R$ %.2f\n", this.limiteChequeEspecial);
        System.out.println("Data de Criação: " + this.dataCriacao.format(getFormatter()));
        System.out.printf("Número de Transações Efetuadas neste mês: %d\n", this.contadorTransacoesMes);
        if (TRANSACOES_GRATUITAS_MES > this.contadorTransacoesMes) {
            System.out.printf("Número de Transações Gratuitas restantes neste mês: %d\n", TRANSACOES_GRATUITAS_MES - this.contadorTransacoesMes);
        } else {
            System.out.println("Número de Transações Gratuitas restantes neste mês: 0\n");
        }
    }

    public void cobrarTaxaManutencaoMensal(){
        LocalDate dataAtualSimulada = LocalDate.now();
        int mesAtualSimulado = dataAtualSimulada.getMonthValue();
        int anoAtualSimulado = dataAtualSimulada.getYear();
        LocalDate ultimoDiaDoMes = dataAtualSimulada.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate dataCobrancaEsteMes;

        if(this.ultimoAnoCobrancaTaxaManutencao < anoAtualSimulado || (anoAtualSimulado == this.ultimoAnoCobrancaTaxaManutencao && this.ultimoMesCobrancaTaxaManutencao < mesAtualSimulado)){
            //cria a data de cobrança deste mês
            if(this.dataCriacao.getDayOfMonth() > ultimoDiaDoMes.getDayOfMonth()){
                dataCobrancaEsteMes = dataAtualSimulada.with(TemporalAdjusters.lastDayOfMonth());
            } else {
                dataCobrancaEsteMes = LocalDate.of(anoAtualSimulado, mesAtualSimulado, diaAniversarioCobranca);
            }
            
            //registra a última data de cobrança
            LocalDate ultimaDataDeCobranca = LocalDate.of(this.ultimoAnoCobrancaTaxaManutencao, this.ultimoMesCobrancaTaxaManutencao, this.diaAniversarioCobranca);
            if (this.diaAniversarioCobranca > ultimaDataDeCobranca.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth()) {
                ultimaDataDeCobranca = ultimaDataDeCobranca.with(TemporalAdjusters.lastDayOfMonth());
            }

            //calcula a diferença de meses entre a data da última aplicação e a data de aplicação para esse mês
            long quantidadeDeMeses = ChronoUnit.MONTHS.between(ultimaDataDeCobranca, dataCobrancaEsteMes);

            if(quantidadeDeMeses == 1){
                if(dataAtualSimulada.isAfter(dataCobrancaEsteMes) || dataAtualSimulada.isEqual(dataCobrancaEsteMes)){
                    System.out.println("===== COBRANÇA DA TAXA DE MANUTENÇÃO MENSAL =====");
                    double saldoAntesDoRendimento = this.saldo;
                    this.saldo -= TAXA_MANUTENCAO_MENSAL;
                    System.out.printf("Saldo Anterior: R$ %.2f\n", saldoAntesDoRendimento);
                    System.out.printf("Taxa de Manutenção Mensal: R$ %.2f\n", TAXA_MANUTENCAO_MENSAL);
                    System.out.printf("Novo Saldo: R$ %.2f", this.saldo);
                    this.ultimoMesCobrancaTaxaManutencao = mesAtualSimulado;
                    this.ultimoAnoCobrancaTaxaManutencao = anoAtualSimulado;
                    this.contadorTransacoesMes = 0;
                } 
            } else if (quantidadeDeMeses >= 2){
                double saldoAntesDoRendimento = this.saldo;
                this.saldo -= TAXA_MANUTENCAO_MENSAL * (quantidadeDeMeses - 1);
                if(dataAtualSimulada.isAfter(dataCobrancaEsteMes) || dataAtualSimulada.isEqual(dataCobrancaEsteMes)){
                    System.out.println("===== COBRANÇA DA TAXA DE MANUTENÇÃO MENSAL =====");
                    this.saldo -= TAXA_MANUTENCAO_MENSAL;
                    this.ultimoMesCobrancaTaxaManutencao = mesAtualSimulado;
                    this.ultimoAnoCobrancaTaxaManutencao = anoAtualSimulado;
                }
                System.out.printf("Saldo Anterior: R$ %.2f\n", saldoAntesDoRendimento);
                System.out.printf("Taxa de Manutenção Mensal: R$ %.2f\n", TAXA_MANUTENCAO_MENSAL * quantidadeDeMeses);
                System.out.printf("Novo Saldo: R$ %.2f", this.saldo);
                this.contadorTransacoesMes = 0;
            }
        }
    }
}