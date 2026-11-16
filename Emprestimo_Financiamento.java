public class Emprestimo_Financiamento {

    public enum Status { PENDENTE, APROVADO, REPROVADO }

    private static final double COMPROMETIMENTO_MAXIMO = 0.30;

    private final Cliente cliente;
    private final double valorSolicitado;
    private final double taxaJurosMensal;
    private final int parcelas;
    private Status status = Status.PENDENTE;
    private String motivo = "Aguardando analise";

    public Emprestimo_Financiamento(Cliente cliente, double valorSolicitado, double taxaJurosMensal, int parcelas) {
        if (valorSolicitado <= 0 || parcelas <= 0 || taxaJurosMensal < 0) {
            throw new IllegalArgumentException("Dados de emprestimo invalidos.");
        }
        this.cliente = cliente;
        this.valorSolicitado = valorSolicitado;
        this.taxaJurosMensal = taxaJurosMensal;
        this.parcelas = parcelas;
    }

    public double getValorParcela() {
        if (taxaJurosMensal == 0) return valorSolicitado / parcelas;
        double fator = Math.pow(1 + taxaJurosMensal, parcelas);
        return valorSolicitado * (taxaJurosMensal * fator) / (fator - 1);
    }

    public double getTotalAPagar() { return getValorParcela() * parcelas; }

    public Status analisar() {
        if (cliente.getNivelKyc() < 2) {
            status = Status.REPROVADO;
            motivo = "KYC insuficiente (minimo nivel 2)";
        } else if (getValorParcela() > cliente.getRendaReferencia() * COMPROMETIMENTO_MAXIMO) {
            status = Status.REPROVADO;
            motivo = String.format("Parcela de R$ %.2f excede 30%% da renda (R$ %.2f)",
                    getValorParcela(), cliente.getRendaReferencia() * COMPROMETIMENTO_MAXIMO);
        } else {
            status = Status.APROVADO;
            motivo = "Dentro do limite de comprometimento de renda";
        }
        return status;
    }

    public void liberarCredito(Conta_Bancaria conta) {
        if (status != Status.APROVADO) {
            throw new Conta_Bancaria.OperacaoBancariaException("Emprestimo nao aprovado.");
        }
        conta.creditar(valorSolicitado, "Credito de emprestimo (" + parcelas + "x)");
    }

    public Cliente getCliente() { return cliente; }
    public double getValorSolicitado() { return valorSolicitado; }
    public double getTaxaJurosMensal() { return taxaJurosMensal; }
    public int getParcelas() { return parcelas; }
    public Status getStatus() { return status; }
    public String getMotivo() { return motivo; }

    @Override
    public String toString() {
        return String.format("Emprestimo de R$ %.2f em %dx de R$ %.2f (%.2f%% a.m.) | %s - %s | Cliente: %s",
                valorSolicitado, parcelas, getValorParcela(), taxaJurosMensal * 100, status, motivo,
                cliente.getNomeCompleto());
    }
}
