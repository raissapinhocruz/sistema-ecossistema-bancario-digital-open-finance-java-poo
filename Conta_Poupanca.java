public class Conta_Poupanca extends Conta_Bancaria {
    private final double taxaRendimentoDiario;
    private final int aniversario;

    public Conta_Poupanca(String agencia, String numero, Cliente titular, String senha,
                          double taxaRendimentoDiario, int aniversario) {
        super(agencia, numero, titular, senha);
        if (aniversario < 1 || aniversario > 28) {
            throw new IllegalArgumentException("Aniversario deve estar entre 1 e 28.");
        }
        this.taxaRendimentoDiario = taxaRendimentoDiario;
        this.aniversario = aniversario;
    }

    @Override
    public double getSaldoDisponivel() { return getSaldo(); }

    @Override
    public String getTipo() { return "Conta Poupanca"; }

    public void aplicarRendimento(int dias) {
        if (dias <= 0 || getSaldo() <= 0) return;
        double rendimento = getSaldo() * (Math.pow(1 + taxaRendimentoDiario, dias) - 1);
        if (rendimento > 0) {
            creditar(rendimento, "Rendimento poupanca (" + dias + " dias)");
        }
    }

    public double getTaxaRendimentoDiario() { return taxaRendimentoDiario; }
    public int getAniversario() { return aniversario; }
}
