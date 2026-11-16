import java.time.LocalDate;

public class Cartao_Debito extends Cartao {
    private final Conta_Bancaria conta;

    public Cartao_Debito(String numero, String nomeImpresso, String cvv, LocalDate validade, Conta_Bancaria conta) {
        super(numero, nomeImpresso, cvv, validade);
        this.conta = conta;
    }

    @Override
    public void comprar(double valor, String estabelecimento) {
        validarCartao(valor);
        conta.debitar(valor, "Compra debito - " + estabelecimento);
    }

    @Override
    public String getTipo() { return "Cartao Debito"; }

    public Conta_Bancaria getConta() { return conta; }
}
