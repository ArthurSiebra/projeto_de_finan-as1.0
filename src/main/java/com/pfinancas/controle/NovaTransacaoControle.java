package com.pfinancas.controle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import com.pfinancas.dao.TransacaoDAO;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;
import com.pfinancas.servico.FinancasServico;
import javafx.scene.Parent;
import java.io.IOException;
import java.time.LocalDate;

public class NovaTransacaoControle {
    
    private TransacaoDAO dao = new TransacaoDAO();
    private FinancasServico servico = new FinancasServico();
    private Alert alerta = new Alert(Alert.AlertType.INFORMATION);
    private Alert alertaerro = new Alert(Alert.AlertType.ERROR);

    @FXML 
    private TextField campoDescricao;

    @FXML 
    private TextField campoValor;

    @FXML
    private ComboBox<TipoTransacao> campoTipo;

    @FXML 
    private DatePicker campoData;

    @FXML
    private Button botaoVoltar;

    @FXML 
    private void initialize(){

        campoTipo.getItems().add(TipoTransacao.RECEITA);
        campoTipo.getItems().add(TipoTransacao.DESPESA);
    }
    @FXML 
    private void salvar(){

        String descricao = campoDescricao.getText();
        String valortext = campoValor.getText();
        TipoTransacao tipo = campoTipo.getValue();
        LocalDate data = campoData.getValue();

        if (descricao.isBlank() || valortext.isBlank() || tipo == null || data == null) {

            alertaerro.setContentText("preencha todos os campos");
            alertaerro.show();
            return ;
        }

        double valor;

       try{
            valor = Double.parseDouble(valortext);
       }

       catch(NumberFormatException e){

            alertaerro.setContentText("digite um valor númerico");
            alertaerro.show();
            return ;
       }

       if(valor<=0){
            alertaerro.setContentText("número inválido");
            alertaerro.show();
            return ;
       }

        Transacao transacao = new Transacao(descricao, valor, tipo, data);

        boolean resultado = servico.realizarTransacao(dao, transacao);

        if (resultado) {      
            alerta.setContentText("Transação realizada com sucesso!");
            alerta.show();
        }
        else{
            alertaerro.setContentText("Saldo insuficiente...");
            alertaerro.show();
        }
    }

    @FXML
    private void voltar() throws IOException {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/pfinancas/view/tela-principal.fxml")
        );

        Parent raiz = loader.load();

        Scene scene = botaoVoltar.getScene();

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

}