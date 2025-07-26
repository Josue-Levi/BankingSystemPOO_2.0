package clients;

import java.util.Objects;

public class PessoaFisica {
    // campo utilzado para identificar tipo de chave no Json
    private final String titularType = "PessoaFisica";

    // Atributos originais
    protected String CPF;
    protected String Nascimento;
    protected String NomePessoa;
    protected String Email;
    protected String Telefone;
    protected String Estado;
    protected String Cidade;
    protected String Bairro;
    protected String Rua;
    protected int Numero;
    protected String Complemento;
    protected String CEP;
    protected String Senha;

    // Construtores
    public PessoaFisica(String NomePessoa, String Nascimento, String CPF, String Email, String Senha, String Telefone){
        this.CPF = CPF;
        this.Nascimento = Nascimento;
        this.NomePessoa = NomePessoa;
        this.Email = Email;
        this.Senha = Senha;
        this.Telefone = Telefone;
    }

    public void setEndereco(String Estado, String Cidade, String Bairro, String Rua, int Numero, String Complemento, String CEP){
        this.Estado = Estado;
        this.Cidade = Cidade;
        this.Bairro = Bairro;
        this.Rua = Rua;
        this.Numero = Numero;
        this.Complemento = Complemento;
        this.CEP = CEP;
    }

    // Getters
    public String getNomePessoa(){
        return NomePessoa;
    }

    public String getCPF(){
        return CPF;
    }

    public String getEmail(){
        return Email;
    }

    public String getSenha(){
        return Senha;
    }

    public String getTelefone() {
        return Telefone;
    }

    public String getEstado() {
        return Estado;
    }

    public String getCidade() {
        return Cidade;
    }

    public String getBairro() {
        return Bairro;
    }

    public String getRua() {
        return Rua;
    }

    public int getNumero() {
        return Numero;
    }

    public String getComplemento() {
        return Complemento;
    }

    public String getCEP() {
        return CEP;
    }

    // Métodos
    public void ExibirDadosPessoaFisica(){
        System.out.println("\nDADOS CADASTRADOS:");
        System.out.println("===== INFORMAÇÕES PESSOAIS =====");
        System.out.println("Nome Cliente: " + NomePessoa);
        System.out.println("CPF: " + CPF);
        System.out.println("Data de Nascimento: " + Nascimento);
        System.out.println("Email: " + Email);
        System.out.println("Senha: " + Senha);
        System.out.println("Telefone: " + Telefone);
        System.out.println("===== INFORMAÇÕES RESIDENCIAL =====");
        System.out.println("Estado: " + Estado);
        System.out.println("Cidade: " + Cidade);
        System.out.println("Bairro: " + Bairro);
        System.out.println("Rua: "+ Rua +" Numero: " + Numero);
        System.out.println("Complemento: " + Complemento);
        System.out.println("CEP: " + CEP);
    }

    // compara para ver se tem outra pessoafisica igual pelo CPF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PessoaFisica that = (PessoaFisica) o;
        return Objects.equals(CPF, that.CPF);
    }
     // gera um codigo hash, baseado no CPF
    @Override
    public int hashCode() {
        return Objects.hash(CPF);
    }
}
