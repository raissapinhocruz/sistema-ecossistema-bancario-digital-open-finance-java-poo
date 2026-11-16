import java.time.LocalDate;

public abstract class Cartao {
    private final String numero;
    private final String nomeImpresso;
    private final String cvv;
    private final LocalDate validade;
    private boolean ativo = true;

    protected Cartao(String numero, String nomeImpresso, String cvv, LocalDate validade) {
        this.numero = numero;
        this.nomeImpresso = nomeImpresso;
        this.cvv = cvv;
        this.validade = validade;
    }

    public abstract void comprar(double valor, String estabelecimento);

    public abstract String getTipo();

    protected void validarCartao(double valor) {
        if (!ativo) {
            throw new Conta_Bancaria.OperacaoBancariaException("Cartao bloqueado.");
        }
        if (validade.isBefore(LocalDate.now())) {
            throw new Conta_Bancaria.OperacaoBancariaException("Cartao vencido.");
        }
        if (valor <= 0) {
            throw new Conta_Bancaria.OperacaoBancariaException("Valor da compra deve ser positivo.");
        }
    }

    public boolean validarCvv(String tentativa) { return cvv.equals(tentativa); }

    public void bloquear() { ativo = false; }
    public void desbloquear() { ativo = true; }
    public boolean isAtivo() { return ativo; }
    public String getNomeImpresso() { return nomeImpresso; }
    public LocalDate getValidade() { return validade; }
    public String getNumero() { return numero; }

    public String getNumeroMascarado() {
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }

    @Override
    public String toString() {
        return String.format("%s %s | %s | Val: %02d/%d | %s", getTipo(), getNumeroMascarado(), nomeImpresso,
                validade.getMonthValue(), validade.getYear(), ativo ? "ATIVO" : "BLOQUEADO");
    }
}
