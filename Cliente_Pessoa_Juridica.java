public class Cliente_Pessoa_Juridica extends Cliente {
    private double faturamentoAnual;
    private final String nomeFantasia;
    private final String ramoAtividade;

    public Cliente_Pessoa_Juridica(int id, String razaoSocial, String cnpj, String endereco, int nivelKyc,
                                   double faturamentoAnual, String nomeFantasia, String ramoAtividade) {
        super(id, razaoSocial, cnpj, endereco, nivelKyc);
        if (faturamentoAnual < 0) {
            throw new IllegalArgumentException("Faturamento nao pode ser negativo.");
        }
        this.faturamentoAnual = faturamentoAnual;
        this.nomeFantasia = nomeFantasia;
        this.ramoAtividade = ramoAtividade;
    }

    public double getFaturamentoAnual() { return faturamentoAnual; }
    public void setFaturamentoAnual(double faturamentoAnual) { this.faturamentoAnual = faturamentoAnual; }
    public String getNomeFantasia() { return nomeFantasia; }
    public String getRamoAtividade() { return ramoAtividade; }

    @Override
    public double getRendaReferencia() { return faturamentoAnual / 12.0; }

    @Override
    public String getTipo() { return "Pessoa Juridica"; }
}
