package com.pfinancas.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;


public class TransacaoTest {
    @Test
    public void transacaoValorCorreto(){
        Transacao t = new Transacao("Sálario", 3000, TipoTransacao.RECEITA, LocalDate.of(2026, 9, 13));

        assertEquals(3000, t.getValor());
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
