package com.pfinancas.app;

import java.time.LocalDate;

import com.pfinancas.model.Transacao;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.servico.FinancasServico;
import com.pfinancas.dao.Conexao;
import com.pfinancas.dao.TransacaoDAO;

import java.sql.Connection;

public class Pfinancas {

    public static void main(String[] args) {

        Transacao t= new Transacao("feira", 200, TipoTransacao.DESPESA, LocalDate.of(2020, 12, 20));
        Transacao t2= new Transacao("Salário", 3000, TipoTransacao.RECEITA, LocalDate.of(2026, 9, 06));
        Transacao t3 = new Transacao("GTA-VI", 500, TipoTransacao.DESPESA, LocalDate.of(2026,11,16));
        Transacao t4 = new Transacao("MESA-FUTMESA", 2500, TipoTransacao.DESPESA, LocalDate.of(2026,9,9));

        FinancasServico servico = new FinancasServico();
        Conexao conexao = new Conexao();
        TransacaoDAO dao = new TransacaoDAO();

        conexao.criaTabela();

        servico.realizarTransacao(dao, t2);
        servico.realizarTransacao(dao,t);
        servico.realizarTransacao(dao, t3);

        boolean resultado = servico.realizarTransacao(dao, t4);
        Connection a = conexao.conectar();
        
        if (resultado == true) {
            System.out.println("Transação realizada");
        }
        else{
            System.out.println("Saldo insuficiente");
        }
        
        if(a == null)
            System.out.println("falhou");

        else
            System.out.println("conexão realizada com sucesso");

        for(Transacao tAtual : dao.listar()){

            System.out.println(tAtual.getDescricao());
            System.out.println(tAtual.getValor());
            System.out.println(tAtual.getTipo());
            System.out.println(tAtual.getData());
            System.out.println("\n");
        }

        double s =  servico.calcularSaldo(dao);
        System.out.println(s);
        System.out.println(t2.getId());
    }
}