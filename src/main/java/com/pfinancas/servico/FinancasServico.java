package com.pfinancas.servico;

import com.pfinancas.dao.TransacaoDAO;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

public class FinancasServico {
    
    public  double calcularSaldo(TransacaoDAO dao){

        double saldo = 0;

        for ( Transacao tAtual : dao.listar()) {

            if (tAtual.getTipo() == TipoTransacao.RECEITA) {
                saldo+= tAtual.getValor();
            }
            else if(tAtual.getTipo() == TipoTransacao.DESPESA){
                saldo-= tAtual.getValor();
            }
        }
     return saldo;
  
    }
    public boolean realizarTransacao(TransacaoDAO dao, Transacao transacao){

        double saldo =  calcularSaldo(dao);
        System.out.println("SALDO ANTES DA TRANSACAO: " + saldo);
        System.out.println("VALOR DA TRANSACAO: " + transacao.getValor());
        System.out.println("TIPO: " + transacao.getTipo());

        if (transacao.getTipo() == TipoTransacao.DESPESA){
            
            if (saldo >= transacao.getValor()) {
                
                dao.inserir(transacao);
                return true;
            }
            else{
                return false;
            }
        }
        else if (transacao.getTipo() == TipoTransacao.RECEITA) {
            dao.inserir(transacao);
            return true;
        }
        return false;
    }
    public boolean removerTransacao(TransacaoDAO dao, Transacao transacao) {

        double saldo = calcularSaldo(dao);

        if (transacao.getTipo() == TipoTransacao.RECEITA) {

            double saldoDepois = saldo - transacao.getValor();

            if (saldoDepois < 0) {
                return false;
            }
        }

        dao.remover(transacao.getId());
        return true;
    }
}
