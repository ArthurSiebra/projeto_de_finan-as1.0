
package com.pfinancas.dto;

import com.pfinancas.model.Categoria;

public record CategoriaResponseDTO(
        Long id,
        String nome
) {

    public static CategoriaResponseDTO fromEntity(
            Categoria categoria) {

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome()
        );
    }
}
