public abstract class Cliente {
    private final int id;
    private final String nomeCompleto;
    private final String documento;
    private String endereco;
    private int nivelKyc;
    private boolean consentimentoOpenFinance;

    protected Cliente(int id, String nomeCompleto, String documento, String endereco, int nivelKyc) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new IllegalArgumentException("Nome obrigatorio.");
        }
        if (nivelKyc < 1 || nivelKyc > 3) {
            throw new IllegalArgumentException("Nivel KYC deve estar entre 1 e 3.");
        }
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.documento = documento;
        this.endereco = endereco;
        this.nivelKyc = nivelKyc;
    }

    public abstract double getRendaReferencia();

    public abstract String getTipo();

    public int getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public int getNivelKyc() { return nivelKyc; }
    public void setNivelKyc(int nivelKyc) {
        if (nivelKyc < 1 || nivelKyc > 3) {
            throw new IllegalArgumentException("Nivel KYC deve estar entre 1 e 3.");
        }
        this.nivelKyc = nivelKyc;
    }
    public boolean isConsentimentoOpenFinance() { return consentimentoOpenFinance; }
    public void setConsentimentoOpenFinance(boolean consentimento) { this.consentimentoOpenFinance = consentimento; }

    public String getDocumentoMascarado() {
        if (documento == null || documento.length() < 4) {
            return "****";
        }
        return "****" + documento.substring(documento.length() - 4);
    }

    String getDocumento() { return documento; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) | Doc: %s | KYC nivel %d", id, nomeCompleto, getTipo(),
                getDocumentoMascarado(), nivelKyc);
    }
}
