
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
import com.pfinancas.repository.CategoriaRepository;
import com.pfinancas.repository.TransacaoRepository;
import com.pfinancas.repository.UsuarioRepository;

import com.pfinancas.exception.SaldoInsuficienteException;
import com.pfinancas.model.Categoria;
import com.pfinancas.model.Usuario;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    private TransacaoService transacaoService;

    @BeforeEach
    void configurar() {
        MockitoAnnotations.openMocks(this);

        transacaoService = new TransacaoService(
            transacaoRepository,
            usuarioRepository,
            categoriaRepository
        );
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


@Test
void deveCadastrarReceita() {

    Long usuarioId = 1L;
    Long categoriaId = 2L;

    Usuario usuario = new Usuario(
            "Arthur",
            "arthur@teste.com",
            "senha"
    );

    Categoria categoria = new Categoria(
            "Trabalho",
            usuario
    );

    Transacao receita = new Transacao(
            "Salário",
            3000.00,
            TipoTransacao.RECEITA,
            LocalDate.now()
    );

    when(usuarioRepository.findById(usuarioId))
            .thenReturn(Optional.of(usuario));

    when(categoriaRepository.findByIdAndUsuarioId(
            categoriaId, usuarioId))
            .thenReturn(Optional.of(categoria));

    when(transacaoRepository.save(any(Transacao.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Transacao resultado = transacaoService.cadastrarTransacao(
            usuarioId, categoriaId, receita
    );

    assertEquals("Salário", resultado.getDescricao());
    assertEquals(usuario, resultado.getUsuario());
    assertEquals(categoria, resultado.getCategoria());

    verify(transacaoRepository).save(receita);
}

@Test
void deveImpedirDespesaComSaldoInsuficiente() {

    Long usuarioId = 1L;
    Long categoriaId = 2L;

    Usuario usuario = new Usuario(
            "Arthur",
            "arthur@teste.com",
            "senha"
    );

    Categoria categoria = new Categoria(
            "Alimentação",
            usuario
    );

    Transacao despesa = new Transacao(
            "Compras",
            2000.00,
            TipoTransacao.DESPESA,
            LocalDate.now()
    );

    Transacao receitaAnterior = new Transacao(
            "Salário",
            1500.00,
            TipoTransacao.RECEITA,
            LocalDate.now()
    );

    when(usuarioRepository.findById(usuarioId))
            .thenReturn(Optional.of(usuario));

    when(categoriaRepository.findByIdAndUsuarioId(
            categoriaId, usuarioId))
            .thenReturn(Optional.of(categoria));

    when(transacaoRepository.findByUsuarioId(usuarioId))
            .thenReturn(List.of(receitaAnterior));

    assertThrows(
            SaldoInsuficienteException.class,
            () -> transacaoService.cadastrarTransacao(
                    usuarioId, categoriaId, despesa
            )
    );
}

    @Test
    void deveRejeitarCategoriaDeOutroUsuario() {

        Long usuarioId = 1L;
        Long categoriaId = 5L;

        Usuario usuario = new Usuario(
            "Arthur",
            "arthur@teste.com",
            "senha"
        );

        Transacao receita = new Transacao(
            "Freelance",
            500.00,
            TipoTransacao.RECEITA,
            LocalDate.now()
        );

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(categoriaRepository.findByIdAndUsuarioId(
                categoriaId, usuarioId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> transacaoService.cadastrarTransacao(
                        usuarioId, categoriaId, receita
                )
        );
    }

}
