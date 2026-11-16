
package com.pfinancas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    Optional<Transacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(
        Long usuarioId,
        TipoTransacao tipo
    );

    List<Transacao> findByUsuarioIdAndDataBetween(
        Long usuarioId,
        LocalDate inicio,
        LocalDate fim
    );

    List<Transacao> findByUsuarioIdAndCategoriaId(
        Long usuarioId,
        Long categoriaId
    );
}
