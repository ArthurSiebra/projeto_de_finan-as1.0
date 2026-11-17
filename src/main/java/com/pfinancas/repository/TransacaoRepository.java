
package com.pfinancas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

public interface TransacaoRepository
        extends JpaRepository<Transacao, Long> {

    // LISTAR TRANSACOES DO USUARIO
    List<Transacao> findByUsuarioId(Long usuarioId);

    // BUSCAR TRANSACAO POR ID E USUARIO
    Optional<Transacao> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    // FILTRAR POR TIPO
    List<Transacao> findByUsuarioIdAndTipo(
            Long usuarioId,
            TipoTransacao tipo
    );

    // FILTRAR POR PERIODO
    List<Transacao> findByUsuarioIdAndDataBetween(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    );

    // FILTRAR POR CATEGORIA
    List<Transacao> findByUsuarioIdAndCategoriaId(
            Long usuarioId,
            Long categoriaId
    );

    // JPQL - RELATORIO POR PERIODO
    @Query("""
        SELECT t
        FROM Transacao t
        WHERE t.usuario.id = :usuarioId
          AND t.data BETWEEN :inicio AND :fim
        ORDER BY t.data DESC
        """)
    List<Transacao> buscarPorPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    // JPQL - RELATORIO POR CATEGORIA
    @Query("""
        SELECT t
        FROM Transacao t
        WHERE t.usuario.id = :usuarioId
          AND t.categoria.id = :categoriaId
        ORDER BY t.data DESC
        """)
    List<Transacao> buscarPorCategoria(
            @Param("usuarioId") Long usuarioId,
            @Param("categoriaId") Long categoriaId
    );

    // JPQL - RELATORIO POR TIPO
    @Query("""
        SELECT t
        FROM Transacao t
        WHERE t.usuario.id = :usuarioId
          AND t.tipo = :tipo
        ORDER BY t.data DESC
        """)
    List<Transacao> buscarPorTipo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoTransacao tipo
    );

    // JPQL - SOMAR RECEITAS OU DESPESAS
    @Query("""
        SELECT COALESCE(SUM(t.valor), 0)
        FROM Transacao t
        WHERE t.usuario.id = :usuarioId
          AND t.tipo = :tipo
        """)
    BigDecimal somarPorTipo(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoTransacao tipo
    );
}
