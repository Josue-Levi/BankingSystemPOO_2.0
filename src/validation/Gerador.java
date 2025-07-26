 package validation;

import java.util.Random;

public class Gerador {

    // atributos armazena dados da conta
    private String numeroConta;
    private final String agencia;
    private final String codigoBanco;
    private final String codigoOperar;
    private static final Random random = new Random();

    // construtor inicializa o objeto Gerador e define os valores fixo e chama o metodo gerar numero.
    public Gerador(){
        this.agencia = "1430";
        this.codigoBanco = "345";
        this.codigoOperar = "001";
        this.numeroConta = gerarConta();
    }
    //Getters para acessar os dados
    public String getNumeroConta(){
        return numeroConta;
    }

    public String getAgencia(){
        return agencia;
    }

    public String getCodigoBanco(){
        return codigoBanco;
    }

    public String getCodigoOperar(){
        return codigoOperar;
    }

    /*metodo privado responsavel por gerar o numero da conta.
    O digito criado é baseado na soma da agencia + operação + conta)
    No final ele retorna o digito formatado*/
    private String gerarConta(){
        StringBuilder contaGerar = new StringBuilder();
        for (int i = 0; i < 8; i++){
            contaGerar.append(random.nextInt(10));
        }
        String numeroContaGerar = contaGerar.toString();

        // O dígito verificador é calculado com base na agência, operação e número da conta
        String digito = agencia + codigoOperar + numeroContaGerar;

        int somaDigitos = 0;

        for (int i = 0; i < digito.length(); i++){
            somaDigitos += Character.getNumericValue(digito.charAt(i));
        }

        int digitoVerificador = somaDigitos % 10;

        return numeroContaGerar + "-" + digitoVerificador;
    }

    // utilizado para formatar os dados para exibição ao usuario
    @Override
    public String toString() {
        return "\n===== CONTA GERADA =====\n" +
                "Banco: " + this.codigoBanco + "\nAgência: " + this.agencia +
                "\nOperação: " + this.codigoOperar + "\nNúmero da Conta: " + this.numeroConta;
    }
}