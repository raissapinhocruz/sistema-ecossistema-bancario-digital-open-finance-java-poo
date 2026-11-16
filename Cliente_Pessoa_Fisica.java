import java.time.LocalDate;
import java.time.Period;

public class Cliente_Pessoa_Fisica extends Cliente {
    private final LocalDate dataNascimento;
    private double rendaMensal;
    private String estadoCivil;

    public Cliente_Pessoa_Fisica(int id, String nome, String cpf, String endereco, int nivelKyc,
                                 LocalDate dataNascimento, double rendaMensal, String estadoCivil) {
        super(id, nome, cpf, endereco, nivelKyc);
        if (rendaMensal < 0) {
            throw new IllegalArgumentException("Renda nao pode ser negativa.");
        }
        this.dataNascimento = dataNascimento;
        this.rendaMensal = rendaMensal;
        this.estadoCivil = estadoCivil;
    }

    public int getIdade() { return Period.between(dataNascimento, LocalDate.now()).getYears(); }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public double getRendaMensal() { return rendaMensal; }
    public void setRendaMensal(double rendaMensal) { this.rendaMensal = rendaMensal; }
    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    @Override
    public double getRendaReferencia() { return rendaMensal; }

    @Override
    public String getTipo() { return "Pessoa Fisica"; }
}
