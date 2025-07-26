package validation;

public class ValidadorCPF {

    // Método estático para validar CPF
    public static boolean validarCPF(String cpf) {
        String cpfDigitos = cpf.replaceAll("[^0-9]", "");

        // CPF deve ter 11 dígitos
        if (cpfDigitos.length() != 11) {
            System.out.println("CPF inválido: Deve conter 11 dígitos numéricos.");
            return false;
        }

        // Evita CPFs com todos os dígitos iguais (ex: 111.111.111-11)
        if (cpfDigitos.matches("(\\d)\\1{10}")) {
            System.out.println("CPF inválido: Não pode ter todos os dígitos iguais.");
            return false;
        }

        int[] digitos = new int[11];
        for (int i = 0; i < 11; i++){
            digitos[i] = Character.getNumericValue(cpfDigitos.charAt(i));
        }

        int soma = 0, peso = 10;

        for (int i = 0; i < 9; i++){
            soma += digitos[i] * peso;
            peso--;
        }

        int resto = soma % 11, divisao1;

        if (resto < 2) divisao1 = 0;
        else divisao1 = 11 - resto;

        soma = 0;
        peso = 11;

        for (int i = 0; i < 10; i++){
            soma += digitos[i] * peso;
            peso--;
        }

        resto = soma % 11;
        int divisao2;

        if (resto < 2) divisao2 = 0;
        else divisao2 = 11 - resto;

        int digitoCPF1 = Character.getNumericValue(cpfDigitos.charAt(9)); // Posição 9 (10º dígito)
        int digitoCPF2 = Character.getNumericValue(cpfDigitos.charAt(10)); // Posição 10 (11º dígito)

        if (divisao1 == digitoCPF1 && divisao2 == digitoCPF2) {
            System.out.println("CPF válido.");
            return true;
        } else {
            System.out.println("CPF inválido. Os dígitos verificadores não correspondem. Tente novamente.");
            return false;
        }
    }
}