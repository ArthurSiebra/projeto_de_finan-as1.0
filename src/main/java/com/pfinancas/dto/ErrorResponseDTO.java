
package com.pfinancas.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem
) {
}
