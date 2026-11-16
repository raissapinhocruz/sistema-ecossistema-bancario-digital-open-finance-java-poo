import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Conta_Bancaria {

    public static class OperacaoBancariaException extends RuntimeException {
        public OperacaoBancariaException(String mensagem) {
            super(mensagem);
        }
    }

    private static final int MAX_TENTATIVAS = 3;

    private final String agencia;
    private final String numero;
    private final Cliente titular;
    private double saldo;
    private final String senha;
    private final List<Transacao> extrato = new ArrayList<>();
    private int tentativasFalhas;
    private boolean bloqueada;

    protected Conta_Bancaria(String agencia, String numero, Cliente titular, String senha) {
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter ao menos 4 caracteres.");
        }
        this.agencia = agencia;
        this.numero = numero;
        this.titular = titular;
        this.senha = senha;
    }

    public abstract double getSaldoDisponivel();

    public abstract String getTipo();

    public boolean autenticar(String tentativa) {
        if (bloqueada) {
            throw new OperacaoBancariaException("Conta " + numero + " bloqueada por seguranca.");
        }
        if (senha.equals(tentativa)) {
            tentativasFalhas = 0;
            return true;
        }
        tentativasFalhas++;
        if (tentativasFalhas >= MAX_TENTATIVAS) {
            bloqueada = true;
        }
        return false;
    }

    public boolean isBloqueada() { return bloqueada; }
    public int getTentativasFalhas() { return tentativasFalhas; }
    public void desbloquear() { bloqueada = false; tentativasFalhas = 0; }

    public void depositar(double valor) {
        validarValor(valor);
        creditar(valor, "Deposito");
    }

    public void sacar(double valor, String senhaInformada) {
        validarValor(valor);
        if (!autenticar(senhaInformada)) {
            throw new OperacaoBancariaException("Senha incorreta.");
        }
        debitar(valor, "Saque");
    }

    void creditar(double valor, String descricao) {
        validarValor(valor);
        saldo += valor;
        extrato.add(new Transacao(valor, descricao));
    }

    void debitar(double valor, String descricao) {
        validarValor(valor);
        if (valor > getSaldoDisponivel()) {
            throw new OperacaoBancariaException(String.format(
                    "Saldo insuficiente na conta %s (disponivel: R$ %.2f, solicitado: R$ %.2f).",
                    numero, getSaldoDisponivel(), valor));
        }
        saldo -= valor;
        extrato.add(new Transacao(-valor, descricao));
    }

    private void validarValor(double valor) {
        if (valor <= 0) {
            throw new OperacaoBancariaException("Valor da operacao deve ser positivo.");
        }
    }

    public String getAgencia() { return agencia; }
    public String getNumero() { return numero; }
    public Cliente getTitular() { return titular; }
    public double getSaldo() { return saldo; }
    public List<Transacao> getExtrato() { return Collections.unmodifiableList(extrato); }

    @Override
    public String toString() {
        return String.format("%s Ag %s / Cc %s | Titular: %s | Saldo: R$ %.2f",
                getTipo(), agencia, numero, titular.getNomeCompleto(), saldo);
    }
}
