
package com.pfinancas.dto;

import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoTransacao tipo,
        LocalDate data,
        Long categoriaId
) {

    public static TransacaoResponseDTO fromEntity(
            Transacao transacao) {

        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getTipo(),
                transacao.getData(),
                transacao.getCategoria().getId()
        );
    }
}
