
package com.pfinancas.servico;

import java.math.BigDecimal;

import com.pfinancas.dao.TransacaoDAO;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

public class FinancasServico {

    public BigDecimal calcularSaldo(TransacaoDAO dao) {

        BigDecimal saldo = BigDecimal.ZERO;

        for (Transacao tAtual : dao.listar()) {

            BigDecimal valor = tAtual.getValor();

            if (tAtual.getTipo() == TipoTransacao.RECEITA) {
                saldo = saldo.add(valor);
            }
            else if (tAtual.getTipo() == TipoTransacao.DESPESA) {
                saldo = saldo.subtract(valor);
            }
        }

        return saldo;
    }

    public boolean realizarTransacao(
            TransacaoDAO dao,
            Transacao transacao) {

        BigDecimal saldo = calcularSaldo(dao);

        System.out.println("SALDO ANTES DA TRANSACAO: " + saldo);
        System.out.println("VALOR DA TRANSACAO: " + transacao.getValor());
        System.out.println("TIPO: " + transacao.getTipo());

        if (transacao.getTipo() == TipoTransacao.DESPESA) {

            if (saldo.compareTo(transacao.getValor()) >= 0) {
                dao.inserir(transacao);
                return true;
            }

            return false;
        }
        else if (transacao.getTipo() == TipoTransacao.RECEITA) {
            dao.inserir(transacao);
            return true;
        }

        return false;
    }

    public boolean removerTransacao(
            TransacaoDAO dao,
            Transacao transacao) {

        BigDecimal saldo = calcularSaldo(dao);

        if (transacao.getTipo() == TipoTransacao.RECEITA) {

            BigDecimal saldoDepois =
                    saldo.subtract(transacao.getValor());

            if (saldoDepois.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }

        dao.remover(Math.toIntExact(transacao.getId()));
        return true;
    }
}
