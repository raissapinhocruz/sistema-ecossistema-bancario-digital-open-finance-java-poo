import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class Banco_Sistema_Central {
    private static final String AGENCIA = "0001";
    private static final double TARIFA_TED = 8.50;
    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    private final String nome;
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Conta_Bancaria> contas = new ArrayList<>();
    private final List<Cartao> cartoes = new ArrayList<>();
    private final List<Emprestimo_Financiamento> emprestimos = new ArrayList<>();
    private final Auditoria_Seguranca auditoria = new Auditoria_Seguranca();

    private int proximoIdCliente = 1;
    private int proximoNumeroConta = 10000;
    private int proximoNumeroCartao = 1000;

    public Banco_Sistema_Central(String nome) {
        this.nome = nome;
        auditoria.registrarConformidade("Core bancario '" + nome + "' iniciado.");
    }

    public Cliente_Pessoa_Fisica cadastrarPessoaFisica(String nome, String cpf, String endereco, int kyc,
                                                       LocalDate nascimento, double renda, String estadoCivil) {
        validarDocumentoUnico(cpf);
        Cliente_Pessoa_Fisica c = new Cliente_Pessoa_Fisica(proximoIdCliente++, nome, cpf, endereco, kyc,
                nascimento, renda, estadoCivil);
        if (c.getIdade() < 18) {
            proximoIdCliente--;
            throw new IllegalArgumentException("Cliente PF deve ser maior de idade.");
        }
        clientes.add(c);
        auditoria.registrarConformidade("Cliente PF cadastrado: " + nome + " (KYC " + kyc + ")");
        return c;
    }

    public Cliente_Pessoa_Juridica cadastrarPessoaJuridica(String razao, String cnpj, String endereco, int kyc,
                                                           double faturamento, String fantasia, String ramo) {
        validarDocumentoUnico(cnpj);
        Cliente_Pessoa_Juridica c = new Cliente_Pessoa_Juridica(proximoIdCliente++, razao, cnpj, endereco, kyc,
                faturamento, fantasia, ramo);
        clientes.add(c);
        auditoria.registrarConformidade("Cliente PJ cadastrado: " + razao + " (KYC " + kyc + ")");
        return c;
    }

    private void validarDocumentoUnico(String doc) {
        for (Cliente c : clientes) {
            if (c.getDocumento().equals(doc)) {
                throw new IllegalArgumentException("Documento ja cadastrado.");
            }
        }
    }

    private String novoNumeroConta() { return String.valueOf(proximoNumeroConta++); }

    private <T extends Conta_Bancaria> T registrarConta(T conta) {
        contas.add(conta);
        auditoria.registrarConformidade(conta.getTipo() + " " + conta.getNumero() + " aberta para "
                + conta.getTitular().getNomeCompleto());
        return conta;
    }

    public Conta_Corrente abrirContaCorrente(Cliente titular, String senha, double limite, double taxa) {
        return registrarConta(new Conta_Corrente(AGENCIA, novoNumeroConta(), titular, senha, limite, taxa));
    }

    public Conta_Poupanca abrirContaPoupanca(Cliente titular, String senha, double taxaDiaria, int aniversario) {
        return registrarConta(new Conta_Poupanca(AGENCIA, novoNumeroConta(), titular, senha, taxaDiaria, aniversario));
    }

    public Conta_Investimento abrirContaInvestimento(Cliente titular, String senha,
                                                     Conta_Investimento.PerfilRisco perfil, double corretagem) {
        return registrarConta(new Conta_Investimento(AGENCIA, novoNumeroConta(), titular, senha, perfil, corretagem));
    }

    public Cartao_Debito emitirCartaoDebito(Conta_Bancaria conta) {
        Cartao_Debito c = new Cartao_Debito(gerarNumeroCartao(), conta.getTitular().getNomeCompleto().toUpperCase(),
                gerarCvv(), LocalDate.now().plusYears(5), conta);
        cartoes.add(c);
        auditoria.registrarConformidade("Cartao de debito emitido para a conta " + conta.getNumero());
        return c;
    }

    public Cartao_Credito emitirCartaoCredito(Cliente titular, double limite, int melhorDia) {

        if (titular.getNivelKyc() < 2) {
            throw new Conta_Bancaria.OperacaoBancariaException("KYC insuficiente para emissao de cartao de credito.");
        }
        Cartao_Credito c = new Cartao_Credito(gerarNumeroCartao(), titular.getNomeCompleto().toUpperCase(),
                gerarCvv(), LocalDate.now().plusYears(5), limite, melhorDia);
        cartoes.add(c);
        auditoria.registrarConformidade("Cartao de credito emitido para " + titular.getNomeCompleto());
        return c;
    }

    private String gerarNumeroCartao() { return String.format("5400123400%06d", proximoNumeroCartao++); }
    private String gerarCvv() { return String.format("%03d", 100 + (proximoNumeroCartao * 7) % 900); }

    public boolean realizarCompra(Cartao cartao, double valor, String estabelecimento) {
        try {
            cartao.comprar(valor, estabelecimento);
            System.out.printf(BR, "  Compra aprovada: R$ %,.2f em %s (%s)%n", valor, estabelecimento, cartao.getTipo());
            return true;
        } catch (Conta_Bancaria.OperacaoBancariaException e) {
            auditoria.registrar(Auditoria_Seguranca.Nivel.ALERTA, "Compra negada: " + e.getMessage());
            System.out.println("  Compra NEGADA: " + e.getMessage());
            return false;
        }
    }

    public void depositar(Conta_Bancaria conta, double valor) {
        conta.depositar(valor);
    }

    public boolean sacar(Conta_Bancaria conta, double valor, String senha) {
        try {
            conta.sacar(valor, senha);
            return true;
        } catch (Conta_Bancaria.OperacaoBancariaException e) {
            tratarFalha(conta, e);
            return false;
        }
    }

    public boolean transferir(Conta_Bancaria origem, Conta_Bancaria destino, double valor, String senha,
                              String modalidade) {
        try {
            if (origem == destino) {
                throw new Conta_Bancaria.OperacaoBancariaException("Origem e destino iguais.");
            }
            if (!origem.autenticar(senha)) {
                throw new Conta_Bancaria.OperacaoBancariaException("Senha incorreta.");
            }
            double tarifa = modalidade.equalsIgnoreCase("TED") ? TARIFA_TED : 0.0;
            if (valor + tarifa > origem.getSaldoDisponivel()) {
                throw new Conta_Bancaria.OperacaoBancariaException("Saldo insuficiente para " + modalidade + ".");
            }
            origem.debitar(valor, modalidade + " enviado para " + destino.getNumero());
            if (tarifa > 0) {
                origem.debitar(tarifa, "Tarifa TED");
            }
            destino.creditar(valor, modalidade + " recebido de " + origem.getNumero());
            auditoria.registrarConformidade(String.format(BR, "%s de R$ %,.2f: %s -> %s", modalidade, valor,
                    origem.getNumero(), destino.getNumero()));
            return true;
        } catch (Conta_Bancaria.OperacaoBancariaException e) {
            tratarFalha(origem, e);
            return false;
        }
    }

    private void tratarFalha(Conta_Bancaria conta, Conta_Bancaria.OperacaoBancariaException e) {
        System.out.println("  Operacao NEGADA: " + e.getMessage());
        if (e.getMessage().startsWith("Senha")) {
            auditoria.registrarAcessoSuspeito(conta.getNumero(), conta.getTentativasFalhas());
            if (conta.isBloqueada()) {
                auditoria.registrarBloqueio(conta.getNumero());
            }
        } else {
            auditoria.registrar(Auditoria_Seguranca.Nivel.INFO, "Operacao negada: " + e.getMessage());
        }
    }

    public Emprestimo_Financiamento solicitarEmprestimo(Cliente cliente, Conta_Bancaria contaDestino,
                                                        double valor, double juros, int parcelas) {
        Emprestimo_Financiamento e = new Emprestimo_Financiamento(cliente, valor, juros, parcelas);
        e.analisar();
        emprestimos.add(e);
        if (e.getStatus() == Emprestimo_Financiamento.Status.APROVADO) {
            e.liberarCredito(contaDestino);
        }
        auditoria.registrarConformidade("Analise de credito de " + cliente.getNomeCompleto() + ": " + e.getStatus());
        return e;
    }

    public void compartilharDadosOpenFinance(Cliente cliente, String instituicaoReceptora) {
        if (!cliente.isConsentimentoOpenFinance()) {
            auditoria.registrar(Auditoria_Seguranca.Nivel.ALERTA,
                    "Compartilhamento bloqueado (sem consentimento): " + cliente.getNomeCompleto());
            System.out.println("  Open Finance BLOQUEADO: cliente nao deu consentimento.");
            return;
        }
        int qtd = 0;
        double total = 0;
        for (Conta_Bancaria c : contas) {
            if (c.getTitular() == cliente) {
                qtd++;
                total += c.getSaldo();
            }
        }
        auditoria.registrarConformidade(String.format(BR,
                "Open Finance: dados de %s compartilhados com %s (%d contas, saldo consolidado R$ %,.2f)",
                cliente.getNomeCompleto(), instituicaoReceptora, qtd, total));
        System.out.println("  Open Finance: dados compartilhados com " + instituicaoReceptora + ".");
    }

    public void exibirExtratoConsolidado(Cliente cliente) {
        System.out.println("---- EXTRATO CONSOLIDADO: " + cliente.getNomeCompleto() + " ----");
        double total = 0;
        for (Conta_Bancaria c : contas) {
            if (c.getTitular() != cliente) continue;
            System.out.println(c);
            for (Transacao t : c.getExtrato()) {
                System.out.println("    " + t);
            }
            total += c.getSaldo();
        }
        System.out.printf(BR, "Saldo consolidado: R$ %,.2f%n", total);
    }

    public void exibirBalancoGeral() {
        System.out.println("---- BALANCO GERAL - " + nome + " ----");
        System.out.println("Clientes: " + clientes.size() + " | Contas: " + contas.size()
                + " | Cartoes: " + cartoes.size() + " | Emprestimos: " + emprestimos.size());
        double total = 0;
        for (Conta_Bancaria c : contas) {
            total += c.getSaldo();
        }
        System.out.printf(BR, "Total de depositos sob custodia: R$ %,.2f%n", total);
        for (Emprestimo_Financiamento e : emprestimos) {
            System.out.println("  " + e);
        }
    }

    public void exibirRelatorioAuditoria() { auditoria.exibirRelatorio(); }

    public List<Cliente> getClientes() { return new ArrayList<>(clientes); }
    public List<Conta_Bancaria> getContas() { return new ArrayList<>(contas); }
    public List<Cartao> getCartoes() { return new ArrayList<>(cartoes); }
    public Auditoria_Seguranca getAuditoria() { return auditoria; }
}
