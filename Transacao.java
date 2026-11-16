import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Transacao {
    private static int contador = 1000;

    private final int codigo;
    private final LocalDateTime dataHora;
    private final double valor;
    private final String descricao;

    public Transacao(double valor, String descricao) {
        this.codigo = ++contador;
        this.dataHora = LocalDateTime.now();
        this.valor = valor;
        this.descricao = descricao;
    }

    public int getCodigo() { return codigo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public double getValor() { return valor; }
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return String.format(Locale.forLanguageTag("pt-BR"), "#%d | %s | %-38s | R$ %,10.2f",
                codigo, dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), descricao, valor);
    }
}
