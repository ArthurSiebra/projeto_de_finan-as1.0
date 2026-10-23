package com.pfinancas.dao;

import org.junit.jupiter.api.Test;

import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransacaoDAOTest {

    private Conexao conexaoTeste;
    private Connection conexaoPrincipal;
    private TransacaoDAO dao;

    @BeforeEach 
    public void prepararBanco(){

        String url = "jdbc:sqlite:file:memdb?mode=memory&cache=shared";

        conexaoTeste = new Conexao(url);
        conexaoPrincipal = conexaoTeste.conectar();
        conexaoTeste.criaTabela();

        dao = new TransacaoDAO(conexaoTeste);
    }
    @Test
    public void inserirTransacao(){

        Transacao t = new Transacao(
        "Salário",
        3000,
        TipoTransacao.RECEITA,
        LocalDate.of(2026, 9, 13)
      );

      dao.inserir(t);
      assertEquals(1, dao.listar().size());
    }
    @Test 
    public void apagarTransacao(){

        Transacao t = new Transacao(
        "Salário",
        3000,
        TipoTransacao.RECEITA,
        LocalDate.of(2026, 9, 13)
      );

        dao.inserir(t);
        dao.remover(t.getId());
        assertEquals(0, dao.listar().size());
    }


    @AfterEach 
    public void fecharBanco() throws SQLException{

        if (conexaoPrincipal != null) {
            conexaoPrincipal.close();
        }
    }

}