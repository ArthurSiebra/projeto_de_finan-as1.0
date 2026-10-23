package com.pfinancas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Conexao {

     private String url;

     public Conexao(){
        this.url = "jdbc:sqlite:fintrack.db";
     }

    public Conexao(String url) {
        this.url = url;
    }   


    public Connection conectar(){
        try {

            return DriverManager.getConnection(url);

        } catch (SQLException e) {
            
            System.out.println(e.getMessage());
            return null;
        }

    }
    public void criaTabela(){

        String sql ="CREATE TABLE IF NOT EXISTS transacoes(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "descricao TEXT NOT NULL, " +
                    "valor REAL NOT NULL, " +
                    "tipo TEXT NOT NULL, "+
                    "data TEXT NOT NULL"+
                    ")";

        try(
            Connection conexao = conectar();
            Statement stmt = conexao.createStatement();
        ){
            stmt.executeUpdate(sql);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
