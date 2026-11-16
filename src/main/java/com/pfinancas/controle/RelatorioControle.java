
package com.pfinancas.controle;

import java.io.IOException;
import java.math.BigDecimal;

import com.pfinancas.dao.TransacaoDAO;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;
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

        BigDecimal saldo = servico.calcularSaldo(dao);

        labelSaldo.setText(
            String.format("R$ %.2f", saldo)
        );

        carregarResumo();
    }

    private void carregarResumo() {

        BigDecimal totalReceitas = BigDecimal.ZERO;
        BigDecimal totalDespesas = BigDecimal.ZERO;
        int quantidade = 0;

        StringBuilder texto = new StringBuilder();

        for (Transacao transacao : dao.listar()) {

            quantidade++;

            if (transacao.getTipo() == TipoTransacao.RECEITA) {
                totalReceitas = totalReceitas.add(transacao.getValor());

            } else if (transacao.getTipo() == TipoTransacao.DESPESA) {
                totalDespesas = totalDespesas.add(transacao.getValor());
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
