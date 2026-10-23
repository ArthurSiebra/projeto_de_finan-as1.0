package com.pfinancas.servico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import com.pfinancas.dao.Conexao;
import com.pfinancas.dao.TransacaoDAO;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

public class FinancasServicoTest{

    private Conexao conexaoTeste;
    private Connection conexaoPrincipal;
    private TransacaoDAO dao;
    private FinancasServico servico;

    @BeforeEach
    public void prepararBanco(){

        String url = "jdbc:sqlite:file:memdb?mode=memory&cache=shared";

        conexaoTeste = new Conexao(url);
        conexaoPrincipal = conexaoTeste.conectar();

        conexaoTeste.criaTabela();

        dao = new TransacaoDAO(conexaoTeste);
        servico = new FinancasServico();
    }

    @Test
    public void deveAceitarReceita(){

        Transacao receita = new Transacao("Sálario", 3000, TipoTransacao.RECEITA, LocalDate.of(2026,10,11));

        boolean resultado = servico.realizarTransacao(dao, receita);

        assertTrue(resultado);
        assertEquals(3000, servico.calcularSaldo(dao));
    }

    @Test
    public void deveRejeitarDespesaSemSaldo(){

        Transacao receita = new Transacao(
            "Salário",
            3000,
            TipoTransacao.RECEITA,
            LocalDate.of(2026, 9, 13)
        );

        Transacao despesa = new Transacao("MESA-FUTMESA", 3500, TipoTransacao.DESPESA, LocalDate.of(2026,9,9));
        servico.realizarTransacao(dao, receita);

        boolean resultado = servico.realizarTransacao(dao,despesa);
        assertFalse(resultado);
        assertEquals(3000, servico.calcularSaldo(dao));
    }

    @AfterEach
    public void fecharBanco() throws SQLException{

        if(conexaoPrincipal != null){
            conexaoPrincipal.close();
        }
    }
}