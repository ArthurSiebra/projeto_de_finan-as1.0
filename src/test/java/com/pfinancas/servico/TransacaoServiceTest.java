
package com.pfinancas.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;
import com.pfinancas.repository.TransacaoRepository;

class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    private TransacaoService transacaoService;

    @BeforeEach
    void configurar() {
        MockitoAnnotations.openMocks(this);

        transacaoService =
                new TransacaoService(transacaoRepository);
    }

    @Test
    void deveCalcularSaldoCorretamente() {

        Long usuarioId = 1L;

        Transacao receita = new Transacao(
                "Salário",
                3000.00,
                TipoTransacao.RECEITA,
                LocalDate.now()
        );

        Transacao despesa = new Transacao(
                "Aluguel",
                500.00,
                TipoTransacao.DESPESA,
                LocalDate.now()
        );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(receita, despesa));

        BigDecimal saldo =
                transacaoService.calcularSaldo(usuarioId);

        assertEquals(
                0,
                new BigDecimal("2500.00").compareTo(saldo)
        );
    }

    @Test
    void deveRetornarSaldoZeroSemTransacoes() {

        Long usuarioId = 2L;

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of());

        BigDecimal saldo =
                transacaoService.calcularSaldo(usuarioId);

        assertEquals(0, BigDecimal.ZERO.compareTo(saldo));
    }

    @Test
    void deveListarTransacoesDoUsuario() {

        Long usuarioId = 3L;

        Transacao receita = new Transacao(
                "Freelance",
                800.00,
                TipoTransacao.RECEITA,
                LocalDate.now()
        );

        when(transacaoRepository.findByUsuarioId(usuarioId))
                .thenReturn(List.of(receita));

        List<Transacao> resultado =
                transacaoService.listarPorUsuario(usuarioId);

        assertEquals(1, resultado.size());
        assertEquals("Freelance", resultado.get(0).getDescricao());
    }
}
