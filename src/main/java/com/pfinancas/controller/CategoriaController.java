
package com.pfinancas.controller;

import com.pfinancas.dto.CategoriaDTO;
import com.pfinancas.dto.CategoriaResponseDTO;
import com.pfinancas.servico.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(
    name = "Categorias",
    description = "Gerenciamento das categorias financeiras do usuário."
)
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(
            CategoriaService categoriaService) {

        this.categoriaService = categoriaService;
    }

    // LISTAR CATEGORIAS DO USUARIO
    @Operation(
        summary = "Listar categorias",
        description = "Retorna todas as categorias financeiras "
                + "pertencentes ao usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Categorias listadas com sucesso."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário sem permissão de acesso."
        )
    })
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar(
            @AuthenticationPrincipal Long usuarioId) {

        return ResponseEntity.ok(
                categoriaService.listarDTO(usuarioId)
        );
    }

    // CADASTRAR NOVA CATEGORIA
    @Operation(
        summary = "Cadastrar categoria",
        description = "Cadastra uma nova categoria financeira "
                + "vinculada ao usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Categoria cadastrada com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados da categoria inválidos."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Usuário não autenticado."
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário sem permissão de acesso."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado."
        )
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> cadastrar(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody CategoriaDTO dto) {

        CategoriaResponseDTO resposta =
                categoriaService.cadastrarDTO(usuarioId, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }
}
