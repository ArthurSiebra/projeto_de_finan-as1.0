package com.pfinancas.controle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;
import com.pfinancas.servico.FinancasServico;
import com.pfinancas.dao.TransacaoDAO;

public class TelaPrincipalControle {

    @FXML
    private Button botaoRelatorio;
    @FXML 
    private Button botaoNovaTransacao;

    @FXML
    private TableView<Transacao> tabelaTransacoes;

    @FXML
    private TableColumn<Transacao, String> colunaDescricao;

    @FXML 
    private TableColumn<Transacao, Double> colunaValor;

    @FXML
    private TableColumn<Transacao,TipoTransacao> colunaTipo;

    @FXML
    private TableColumn<Transacao, LocalDate> colunaData;

    @FXML 
    private Label labelSaldo;

    private TransacaoDAO dao = new TransacaoDAO();

    private FinancasServico servico = new FinancasServico();

    private Alert alerta = new Alert(Alert.AlertType.INFORMATION);

    private Alert alertaerro = new Alert(Alert.AlertType.ERROR);

  
   @FXML
    private void novaTransacao() throws IOException {

        FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/com/pfinancas/view/nova-transacao.fxml")
        );

        Parent raiz = loader.load();

        Scene scene = botaoNovaTransacao.getScene();

        FadeTransition fadeSaida = new FadeTransition(
            Duration.millis(200),
            scene.getRoot()
        );

        fadeSaida.setFromValue(1.0);
        fadeSaida.setToValue(0.0);

        fadeSaida.setOnFinished(evento -> {

        raiz.setOpacity(0.0);
        scene.setRoot(raiz);

        FadeTransition fadeEntrada = new FadeTransition(
            Duration.millis(200),
            raiz
        );

        fadeEntrada.setFromValue(0.0);
        fadeEntrada.setToValue(1.0);
        fadeEntrada.play();
    });

    fadeSaida.play();
}
    @FXML 
    private void initialize() {

        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaTipo.setCellValueFactory(new  PropertyValueFactory<>("tipo"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));

        tabelaTransacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        carregarDados();
    }
     
    private void carregarDados(){

        ArrayList<Transacao> lista = dao.listar();
        tabelaTransacoes.getItems().setAll(lista);

        double saldo = servico.calcularSaldo(dao);
        labelSaldo.setText(String.format("Saldo atual: R$ %.2f", saldo));

    }

    @FXML
    private void removerTransacao() {

        Transacao selecionada =
            tabelaTransacoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            alertaerro.setContentText("Transação não selecionada.");
            alertaerro.show();
            return;
        }

        boolean resultado = servico.removerTransacao(dao, selecionada);

        if (resultado) {

            alerta.setContentText("Transação removida com sucesso!"
        );
        alerta.show();

        carregarDados();

        } else {

            alertaerro.setContentText("Não é possível remover essa receita, pois o saldo ficaria negativo.");
            alertaerro.show();
        }
    }
    @FXML
    private void abrirRelatorio() throws IOException {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/pfinancas/view/relatorio.fxml")
        );

        Parent raiz = loader.load();

        Scene scene = botaoRelatorio.getScene();
        scene.setRoot(raiz);
    }
}