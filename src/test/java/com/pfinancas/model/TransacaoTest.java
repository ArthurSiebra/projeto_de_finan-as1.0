package com.pfinancas.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TransacaoTest {
    @Test
    public void transacaoValorCorreto(){
        Transacao t = new Transacao("Sálario", 3000, TipoTransacao.RECEITA, LocalDate.of(2026, 9, 13));

        assertEquals(0, new BigDecimal("3000.00").compareTo(t.getValor()));
    }
    @Test 
    public  void transacaoDescricaoCorreta(){
         Transacao t = new Transacao("Sálario", 3000, TipoTransacao.RECEITA, LocalDate.of(2026, 9, 13));
        assertEquals("Sálario", t.getDescricao());
    }
    @Test
    void naoDeveAceitarValorNegativo(){
        assertThrows(
            IllegalArgumentException.class,
             () -> new Transacao("Compra", -100, TipoTransacao.DESPESA, LocalDate.now()));
    }
}
