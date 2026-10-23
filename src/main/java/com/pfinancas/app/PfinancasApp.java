package com.pfinancas.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import com.pfinancas.dao.Conexao;

public class PfinancasApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Conexao conexao = new Conexao();
        conexao.criaTabela();

        FXMLLoader loader = new FXMLLoader(
        PfinancasApp.class.getResource("/com/pfinancas/view/tela-principal.fxml"));
        
        Parent raiz = loader.load();

        Scene scene = new Scene(raiz, 800,500);

        scene.getStylesheets().add(PfinancasApp.class.getResource("/com/pfinancas/view/estilo.css").toExternalForm());


        stage.setScene(scene);
        stage.setTitle("FinTrack");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    
    }
}