package com.pfinancas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;

import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

public class TransacaoDAO {
 
    private Conexao conexao;

    public TransacaoDAO(){
        this.conexao = new Conexao();
    }

    public  TransacaoDAO(Conexao conexao){
        this.conexao = conexao;
    }

    public void inserir(Transacao transacao){

        String sql = "INSERT INTO transacoes (descricao, valor, tipo, data) VALUES(?, ?, ?, ?)";
        
        try(
            Connection a = conexao.conectar();
            PreparedStatement b = a.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            b.setString(1, transacao.getDescricao());
            b.setDouble(2, transacao.getValor());
            b.setString(3, transacao.getTipo().name());
            b.setString(4, transacao.getData().toString());
            b.executeUpdate();

            try(ResultSet chaves = b.getGeneratedKeys()){
            
                if(chaves.next()){
                    int idGerado = chaves.getInt(1);
                    transacao.setId(idGerado);
                }
            }   
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    
    }
    public ArrayList<Transacao> listar(){

        String sql = "SELECT * FROM transacoes";
        ArrayList<Transacao> transacoes = new ArrayList<>();

        try(
            Connection a = conexao.conectar();
            PreparedStatement b = a.prepareStatement(sql);
            ResultSet resultado = b.executeQuery();
        ){
            while (resultado.next()) {
               String descricao = resultado.getString("descricao");
               double valor = resultado.getDouble("valor");
               String tipo = resultado.getString("tipo");
               String data = resultado.getString("data");

               Transacao transacao = new Transacao(descricao, valor, TipoTransacao.valueOf(tipo), LocalDate.parse(data));
               transacao.setId(resultado.getInt("id"));
               transacoes.add(transacao);
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
      return transacoes;
    }

    public void remover(int id){

        String sql = "DELETE FROM transacoes WHERE id = ?";

        try(
            Connection a = conexao.conectar();
            PreparedStatement b = a.prepareStatement(sql);
        ){
            b.setInt(1, id);
            b.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void atualizar(Transacao transacao){
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, tipo = ?, data = ? WHERE id = ?";
       
        try(
            Connection a = conexao.conectar();
            PreparedStatement b = a.prepareStatement(sql);
        ){
            b.setString(1, transacao.getDescricao());
            b.setDouble(2, transacao.getValor());
            b.setString(3, transacao.getTipo().name());
            b.setString(4, transacao.getData().toString());
            b.setInt(5, transacao.getId());

            b.executeUpdate();
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
}