public class Conta_Corrente extends Conta_Bancaria {
    private double limiteChequeEspecial;
    private final double taxaManutencaoMensal;

    public Conta_Corrente(String agencia, String numero, Cliente titular, String senha,
                          double limiteChequeEspecial, double taxaManutencaoMensal) {
        super(agencia, numero, titular, senha);
        if (limiteChequeEspecial < 0 || taxaManutencaoMensal < 0) {
            throw new IllegalArgumentException("Limite e taxa nao podem ser negativos.");
        }
        this.limiteChequeEspecial = limiteChequeEspecial;
        this.taxaManutencaoMensal = taxaManutencaoMensal;
    }

    @Override
    public double getSaldoDisponivel() { return getSaldo() + limiteChequeEspecial; }

    @Override
    public String getTipo() { return "Conta Corrente"; }

    public void cobrarManutencaoMensal() {
        if (taxaManutencaoMensal > 0) {
            debitar(taxaManutencaoMensal, "Taxa de manutencao mensal");
        }
    }

    public double getLimiteChequeEspecial() { return limiteChequeEspecial; }
    public void setLimiteChequeEspecial(double novoLimite) {
        if (novoLimite < 0) throw new IllegalArgumentException("Limite invalido.");
        this.limiteChequeEspecial = novoLimite;
    }
    public double getTaxaManutencaoMensal() { return taxaManutencaoMensal; }
}
