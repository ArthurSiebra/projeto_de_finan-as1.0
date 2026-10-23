package com.pfinancas.repositorio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import com.pfinancas.model.Transacao;
import com.pfinancas.model.TipoTransacao;

public class RepositorioGenericoTest {

    @Test
    public void deveAdicionarTransacao() {

        RepositorioGenerico<Transacao> repositorio =
            new RepositorioGenerico<>();

        Transacao t = new Transacao(
            "Salário",
            3000,
            TipoTransacao.RECEITA,
            LocalDate.of(2026, 9, 13)
        );

        repositorio.adicionar(t);

        assertEquals(1, repositorio.listar().size());
    }
    @Test 
    public void deveRemoverTransacao(){

        RepositorioGenerico<Transacao> repositorio = new RepositorioGenerico<>();

        Transacao t = new Transacao( "Sálario", 3000, TipoTransacao.RECEITA, LocalDate.of(2026, 9, 13));
        Transacao t2 = new Transacao("feira", 200, TipoTransacao.DESPESA, LocalDate.of(2020, 12, 20));

        repositorio.adicionar(t);
        repositorio.adicionar(t2);

        repositorio.remover(t2);

        assertEquals(1, repositorio.listar().size());

    }
}