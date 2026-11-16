import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Auditoria_Seguranca {

    public enum Nivel { INFO, ALERTA, CRITICO }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final List<String> registros = new ArrayList<>();
    private int alertas;
    private int criticos;

    public void registrar(Nivel nivel, String evento) {
        if (nivel == Nivel.ALERTA) alertas++;
        if (nivel == Nivel.CRITICO) criticos++;
        registros.add(String.format("[%s] %-8s %s", LocalDateTime.now().format(FMT), nivel, evento));
    }

    public void registrarAcessoSuspeito(String numeroConta, int tentativas) {
        registrar(tentativas >= 3 ? Nivel.CRITICO : Nivel.ALERTA,
                "Tentativa de acesso com senha incorreta na conta " + numeroConta + " (tentativa " + tentativas + ")");
    }

    public void registrarBloqueio(String numeroConta) {
        registrar(Nivel.CRITICO, "Conta " + numeroConta + " bloqueada por excesso de tentativas.");
    }

    public void registrarConformidade(String evento) {
        registrar(Nivel.INFO, "[Conformidade BACEN] " + evento);
    }

    public void exibirRelatorio() {
        System.out.println("---- RELATORIO DE AUDITORIA ----");
        registros.forEach(System.out::println);
        System.out.printf("Total de eventos: %d | Alertas: %d | Criticos: %d%n", registros.size(), alertas, criticos);
    }

    public List<String> getRegistros() { return new ArrayList<>(registros); }
}
