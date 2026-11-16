public class Conta_Investimento extends Conta_Bancaria {

    public enum PerfilRisco { CONSERVADOR, MODERADO, ARROJADO }

    private final PerfilRisco perfil;
    private final double taxaCorretagem;
    private double carteiraInvestida;

    public Conta_Investimento(String agencia, String numero, Cliente titular, String senha,
                              PerfilRisco perfil, double taxaCorretagem) {
        super(agencia, numero, titular, senha);
        this.perfil = perfil;
        this.taxaCorretagem = taxaCorretagem;
    }

    @Override
    public double getSaldoDisponivel() { return getSaldo(); }

    @Override
    public String getTipo() { return "Conta Investimento"; }

    public void investir(double valor, String fundo) {
        double corretagem = valor * taxaCorretagem;
        if (perfil == PerfilRisco.CONSERVADOR && valor > getSaldo() * 0.5) {
            throw new OperacaoBancariaException("Perfil CONSERVADOR nao permite aplicar mais de 50% do saldo de uma vez.");
        }
        debitar(valor + corretagem, "Aplicacao em " + fundo + " (+corretagem)");
        carteiraInvestida += valor;
    }

    public void resgatar(double valor, String fundo) {
        if (valor <= 0 || valor > carteiraInvestida) {
            throw new OperacaoBancariaException("Valor de resgate invalido ou superior ao investido.");
        }
        carteiraInvestida -= valor;
        creditar(valor, "Resgate de " + fundo);
    }

    public PerfilRisco getPerfil() { return perfil; }
    public double getTaxaCorretagem() { return taxaCorretagem; }
    public double getCarteiraInvestida() { return carteiraInvestida; }
}
