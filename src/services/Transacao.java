package services;

import model.Conta;
import java.util.List;

public class Transacao {
    public static boolean transferir(List<Conta> contas, String numeroOrigem, String numeroDestino, double valor) {
        Conta origem = null;
        Conta destino = null;

        // procuras as contas.
        for (Conta conta : contas) {
            if (conta.getNumeroDaConta().equals(numeroOrigem)) {
                origem = conta;
            }
            if (conta.getNumeroDaConta().equals(numeroDestino)) {
                destino = conta;
            }
        }

        // verifica se foram encontradas
        if (origem == null || destino == null) {
            System.out.println("Conta de origem ou destino não encontrada.");
            return false;
        }

        // verifica se a conta de origem tem saldo suficiente.
        if (origem.getSaldo() < valor) {
            System.out.println("Saldo insuficiente na conta de origem.");
            return false;
        }

        // se encontra as contas e passar pelas validações, executa a transação.
        origem.sacar(valor);
        destino.depositar(valor);
        System.out.println("Transferência realizada com sucesso.");
        return true;
    }
}
