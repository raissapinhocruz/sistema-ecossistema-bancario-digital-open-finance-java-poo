import java.time.LocalDate;

public class Programa {

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(" " + texto);
        System.out.println("==================================================");
    }

    public static void main(String[] args) {

        Banco_Sistema_Central banco = new Banco_Sistema_Central("Fintech Verde Bank");

        titulo("1. CADASTRO DE CLIENTES");
        Cliente_Pessoa_Fisica ana = banco.cadastrarPessoaFisica("Ana Souza", "12345678901",
                "Rua das Flores, 100 - BH", 3, LocalDate.of(1995, 4, 12), 8000.00, "Solteira");
        Cliente_Pessoa_Fisica bruno = banco.cadastrarPessoaFisica("Bruno Lima", "98765432100",
                "Av. Brasil, 250 - BH", 1, LocalDate.of(2000, 9, 30), 2500.00, "Casado");
        Cliente_Pessoa_Juridica eco = banco.cadastrarPessoaJuridica("EcoSolar Energia Ltda", "11222333000144",
                "Distrito Industrial, 55 - Contagem", 3, 2400000.00, "EcoSolar", "Energia renovavel");
        for (Cliente c : banco.getClientes()) {
            System.out.println(c);
        }

        titulo("2. ABERTURA DE CONTAS");
        Conta_Corrente ccAna = banco.abrirContaCorrente(ana, "1234", 1000.00, 12.90);
        Conta_Poupanca poupAna = banco.abrirContaPoupanca(ana, "1234", 0.0002, 15);
        Conta_Investimento invAna = banco.abrirContaInvestimento(ana, "1234",
                Conta_Investimento.PerfilRisco.MODERADO, 0.005);
        Conta_Corrente ccBruno = banco.abrirContaCorrente(bruno, "4321", 300.00, 0.00);
        Conta_Corrente ccEco = banco.abrirContaCorrente(eco, "9999", 20000.00, 49.90);
        for (Conta_Bancaria c : banco.getContas()) {
            System.out.println(c);
        }

        titulo("3. DEPOSITOS");
        banco.depositar(ccAna, 5000.00);
        banco.depositar(ccBruno, 800.00);
        banco.depositar(ccEco, 50000.00);
        System.out.println("Depositos realizados.");

        titulo("4. CARTOES (DEBITO E CREDITO)");
        Cartao_Debito debAna = banco.emitirCartaoDebito(ccAna);
        Cartao_Credito credAna = banco.emitirCartaoCredito(ana, 3000.00, 10);
        Cartao_Debito debBruno = banco.emitirCartaoDebito(ccBruno);
        System.out.println(debAna);
        System.out.println(credAna);
        try {
            banco.emitirCartaoCredito(bruno, 1000.00, 5);
        } catch (Conta_Bancaria.OperacaoBancariaException e) {
            System.out.println("Bruno: " + e.getMessage());
        }
        banco.realizarCompra(debAna, 250.00, "Supermercado Organico");
        banco.realizarCompra(credAna, 1800.00, "Loja de Eletronicos");
        banco.realizarCompra(credAna, 1500.00, "Loja de Moveis");
        banco.realizarCompra(debBruno, 5000.00, "Concessionaria");
        debBruno.bloquear();
        banco.realizarCompra(debBruno, 20.00, "Padaria");
        System.out.println(credAna);
        credAna.pagarFatura(ccAna);
        System.out.println("Fatura paga. " + credAna);

        titulo("5. SAQUES, PIX E TED");
        banco.sacar(ccAna, 200.00, "1234");
        banco.transferir(ccAna, ccBruno, 500.00, "1234", "PIX");
        banco.transferir(ccEco, ccAna, 3000.00, "9999", "TED");
        banco.transferir(ccAna, poupAna, 1500.00, "1234", "PIX");
        banco.transferir(ccAna, invAna, 1000.00, "1234", "PIX");
        banco.transferir(ccBruno, ccAna, 99999.00, "4321", "PIX");

        System.out.println("-- Tentativas de acesso indevido na conta do Bruno --");
        banco.sacar(ccBruno, 50.00, "0000");
        banco.sacar(ccBruno, 50.00, "1111");
        banco.sacar(ccBruno, 50.00, "2222");
        banco.sacar(ccBruno, 50.00, "4321");

        poupAna.aplicarRendimento(30);
        invAna.investir(400.00, "Fundo Verde ESG");
        invAna.resgatar(100.00, "Fundo Verde ESG");
        ccEco.cobrarManutencaoMensal();
        ccAna.cobrarManutencaoMensal();

        titulo("6. ANALISE DE EMPRESTIMOS");
        Emprestimo_Financiamento e1 = banco.solicitarEmprestimo(ana, ccAna, 12000.00, 0.025, 12);
        Emprestimo_Financiamento e2 = banco.solicitarEmprestimo(bruno, ccBruno, 20000.00, 0.03, 24);
        Emprestimo_Financiamento e3 = banco.solicitarEmprestimo(eco, ccEco, 150000.00, 0.015, 36);
        System.out.println(e1);
        System.out.println(e2);
        System.out.println(e3);

        titulo("7. OPEN FINANCE");
        banco.compartilharDadosOpenFinance(ana, "Instituicao Parceira X");
        ana.setConsentimentoOpenFinance(true);
        banco.compartilharDadosOpenFinance(ana, "Instituicao Parceira X");

        titulo("8. EXTRATOS CONSOLIDADOS");
        banco.exibirExtratoConsolidado(ana);
        System.out.println();
        banco.exibirExtratoConsolidado(bruno);

        titulo("9. BALANCO GERAL");
        banco.exibirBalancoGeral();

        titulo("10. AUDITORIA");
        banco.exibirRelatorioAuditoria();
    }
}
