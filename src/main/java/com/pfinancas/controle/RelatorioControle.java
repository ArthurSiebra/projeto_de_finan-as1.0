package com.pfinancas.controle;

import java.io.IOException;

import com.pfinancas.dao.TransacaoDAO;
import com.pfinancas.model.Transacao;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.servico.FinancasServico;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class RelatorioControle {

    @FXML
    private Label labelSaldo;

    @FXML
    private CheckBox checkDetalhes;

    @FXML
    private TextArea areaResumo;

    @FXML
    private Button botaoVoltar;

    private TransacaoDAO dao = new TransacaoDAO();
    private FinancasServico servico = new FinancasServico();

    @FXML
    private void initialize() {

        double saldo = servico.calcularSaldo(dao);

        labelSaldo.setText(
            String.format("R$ %.2f", saldo)
        );

        carregarResumo();
    }

    private void carregarResumo() {

        double totalReceitas = 0;
        double totalDespesas = 0;
        int quantidade = 0;

        StringBuilder texto = new StringBuilder();

        for (Transacao transacao : dao.listar()) {

            quantidade++;

            if (transacao.getTipo() == TipoTransacao.RECEITA) {
                totalReceitas += transacao.getValor();
            } else if (transacao.getTipo() == TipoTransacao.DESPESA) {
                totalDespesas += transacao.getValor();
            }

            texto.append(transacao.getData())
                 .append(" | ")
                 .append(transacao.getTipo())
                 .append(" | ")
                 .append(transacao.getDescricao())
                 .append(" | R$ ")
                 .append(String.format("%.2f", transacao.getValor()))
                 .append("\n");
        }

        String resumo =
            String.format("Total de receitas: R$ %.2f%n", totalReceitas) +
            String.format("Total de despesas: R$ %.2f%n", totalDespesas) +
            String.format("Total de transações: %d%n%n", quantidade) +
            "TRANSAÇÕES\n" +
            "----------------------------------------\n" +
            texto;

        areaResumo.setText(resumo);
    }

    @FXML
    private void mostrarDetalhes() {

        boolean mostrar = checkDetalhes.isSelected();

        areaResumo.setVisible(mostrar);
        areaResumo.setManaged(mostrar);
    }

    @FXML
    private void voltar() throws IOException {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/pfinancas/view/tela-principal.fxml")
        );

        Parent raiz = loader.load();

        Scene scene = botaoVoltar.getScene();
        scene.setRoot(raiz);
    }
}