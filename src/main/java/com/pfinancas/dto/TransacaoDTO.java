
package com.pfinancas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pfinancas.model.TipoTransacao;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransacaoDTO(

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(
                value = "0.01",
                message = "O valor deve ser maior que zero"
        )
        BigDecimal valor,

        @NotNull(message = "O tipo é obrigatório")
        TipoTransacao tipo,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId
) {
}
