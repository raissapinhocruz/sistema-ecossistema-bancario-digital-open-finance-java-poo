import java.time.LocalDate;

public class Cartao_Credito extends Cartao {
    private final double limiteTotal;
    private double limiteDisponivel;
    private double faturaAtual;
    private final int melhorDiaCompra;

    public Cartao_Credito(String numero, String nomeImpresso, String cvv, LocalDate validade,
                          double limiteTotal, int melhorDiaCompra) {
        super(numero, nomeImpresso, cvv, validade);
        if (limiteTotal <= 0) {
            throw new IllegalArgumentException("Limite deve ser positivo.");
        }
        this.limiteTotal = limiteTotal;
        this.limiteDisponivel = limiteTotal;
        this.melhorDiaCompra = melhorDiaCompra;
    }

    @Override
    public void comprar(double valor, String estabelecimento) {
        validarCartao(valor);
        if (valor > limiteDisponivel) {
            throw new Conta_Bancaria.OperacaoBancariaException(String.format(
                    "Limite insuficiente (disponivel: R$ %.2f, compra: R$ %.2f) em %s.",
                    limiteDisponivel, valor, estabelecimento));
        }
        limiteDisponivel -= valor;
        faturaAtual += valor;
    }

    public void pagarFatura(Conta_Bancaria conta) {
        if (faturaAtual <= 0) {
            throw new Conta_Bancaria.OperacaoBancariaException("Nao ha fatura em aberto.");
        }
        conta.debitar(faturaAtual, "Pagamento de fatura do cartao " + getNumeroMascarado());
        limiteDisponivel = limiteTotal;
        faturaAtual = 0;
    }

    @Override
    public String getTipo() { return "Cartao Credito"; }

    public double getLimiteTotal() { return limiteTotal; }
    public double getLimiteDisponivel() { return limiteDisponivel; }
    public double getFaturaAtual() { return faturaAtual; }
    public int getMelhorDiaCompra() { return melhorDiaCompra; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Limite: R$ %.2f | Disponivel: R$ %.2f | Fatura: R$ %.2f",
                limiteTotal, limiteDisponivel, faturaAtual);
    }
}
