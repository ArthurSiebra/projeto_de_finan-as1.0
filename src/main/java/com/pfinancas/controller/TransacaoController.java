
package com.pfinancas.controller;

import com.pfinancas.dto.SaldoDTO;
import com.pfinancas.dto.TransacaoDTO;
import com.pfinancas.dto.TransacaoResponseDTO;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;
import com.pfinancas.servico.TransacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transacoes")
@Tag(
    name = "Transações",
    description = "Gerenciamento de transações, saldo e relatórios financeiros."
)
@SecurityRequirement(name = "bearerAuth")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(
            TransacaoService transacaoService) {

        this.transacaoService = transacaoService;
    }

    // LISTAR TRANSACOES
    @Operation(
        summary = "Listar transações",
        description = "Retorna todas as transações pertencentes "
                + "ao usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Transações listadas com sucesso."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado.")
    })
    @GetMapping
    public ResponseEntity<List<TransacaoResponseDTO>> listar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId) {

        return ResponseEntity.ok(
                transacaoService.listarPorUsuarioDTO(usuarioId)
        );
    }

    // CONSULTAR SALDO
    @Operation(
        summary = "Consultar saldo",
        description = "Calcula o saldo financeiro do usuário "
                + "considerando receitas e despesas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Saldo consultado com sucesso."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado.")
    })
    @GetMapping("/saldo")
    public ResponseEntity<SaldoDTO> consultarSaldo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId) {

        return ResponseEntity.ok(
                new SaldoDTO(
                        transacaoService.calcularSaldo(usuarioId)
                )
        );
    }

    // CADASTRAR TRANSACAO
    @Operation(
        summary = "Cadastrar transação",
        description = "Registra uma receita ou despesa vinculada "
                + "ao usuário autenticado e a uma de suas categorias. "
                + "Despesas não podem ultrapassar o saldo disponível."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201",
                description = "Transação cadastrada com sucesso."),
        @ApiResponse(responseCode = "400",
                description = "Dados inválidos ou saldo insuficiente."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado."),
        @ApiResponse(responseCode = "404",
                description = "Categoria ou usuário não encontrado.")
    })
    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> cadastrar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody TransacaoDTO dto) {

        Transacao transacao = new Transacao(
                dto.descricao(),
                dto.valor(),
                dto.tipo(),
                dto.data(),
                null,
                null
        );

        TransacaoResponseDTO resposta =
                transacaoService.cadastrarTransacaoDTO(
                        usuarioId,
                        dto.categoriaId(),
                        transacao
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    // ATUALIZAR TRANSACAO
    @Operation(
        summary = "Atualizar transação",
        description = "Atualiza uma transação pertencente ao usuário "
                + "autenticado. Verifica se a alteração deixaria "
                + "o saldo financeiro negativo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Transação atualizada com sucesso."),
        @ApiResponse(responseCode = "400",
                description = "Dados inválidos ou saldo insuficiente."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado."),
        @ApiResponse(responseCode = "404",
                description = "Transação ou categoria não encontrada.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> atualizar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId,
            @Parameter(description = "ID da transação a atualizar",
                    example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TransacaoDTO dto) {

        return ResponseEntity.ok(
                transacaoService.atualizarTransacao(
                        usuarioId,
                        id,
                        dto
                )
        );
    }

    // EXCLUIR TRANSACAO
    @Operation(
        summary = "Excluir transação",
        description = "Exclui uma transação pertencente ao usuário. "
                + "Não permite excluir uma receita caso isso "
                + "deixe o saldo negativo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204",
                description = "Transação excluída com sucesso."),
        @ApiResponse(responseCode = "400",
                description = "A exclusão deixaria o saldo negativo."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado."),
        @ApiResponse(responseCode = "404",
                description = "Transação não encontrada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId,
            @Parameter(description = "ID da transação a excluir",
                    example = "1")
            @PathVariable Long id) {

        transacaoService.excluirTransacao(usuarioId, id);

        return ResponseEntity.noContent().build();
    }

    // RELATORIO POR PERIODO
    @Operation(
        summary = "Relatório por período",
        description = "Lista as transações do usuário entre "
                + "duas datas, incluindo as datas inicial e final."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Relatório gerado com sucesso."),
        @ApiResponse(responseCode = "400",
                description = "Período ou formato de data inválido."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado.")
    })
    @GetMapping("/relatorios/periodo")
    public ResponseEntity<List<TransacaoResponseDTO>> relatorioPeriodo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId,

            @Parameter(description = "Data inicial no formato AAAA-MM-DD",
                    example = "2026-01-01")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @Parameter(description = "Data final no formato AAAA-MM-DD",
                    example = "2026-12-31")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim) {

        return ResponseEntity.ok(
                transacaoService.relatorioPorPeriodo(
                        usuarioId,
                        inicio,
                        fim
                )
        );
    }

    // RELATORIO POR CATEGORIA
    @Operation(
        summary = "Relatório por categoria",
        description = "Lista as transações associadas a uma "
                + "categoria do usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Relatório gerado com sucesso."),
        @ApiResponse(responseCode = "400",
                description = "Parâmetro de categoria inválido."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado.")
    })
    @GetMapping("/relatorios/categoria")
    public ResponseEntity<List<TransacaoResponseDTO>> relatorioCategoria(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId,
            @Parameter(description = "ID da categoria",
                    example = "1")
            @RequestParam Long categoriaId) {

        return ResponseEntity.ok(
                transacaoService.relatorioPorCategoria(
                        usuarioId,
                        categoriaId
                )
        );
    }

    // RELATORIO POR TIPO
    @Operation(
        summary = "Relatório por tipo",
        description = "Lista as transações filtradas por "
                + "RECEITA ou DESPESA."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Relatório gerado com sucesso."),
        @ApiResponse(responseCode = "400",
                description = "Tipo de transação inválido."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado.")
    })
    @GetMapping("/relatorios/tipo")
    public ResponseEntity<List<TransacaoResponseDTO>> relatorioTipo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId,
            @Parameter(description = "Tipo da transação: RECEITA ou DESPESA",
                    example = "RECEITA")
            @RequestParam TipoTransacao tipo) {

        return ResponseEntity.ok(
                transacaoService.relatorioPorTipo(
                        usuarioId,
                        tipo
                )
        );
    }

    // RESUMO FINANCEIRO
    @Operation(
        summary = "Consultar resumo financeiro",
        description = "Retorna o total de receitas, o total "
                + "de despesas e o saldo financeiro do usuário."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Resumo financeiro gerado com sucesso."),
        @ApiResponse(responseCode = "401",
                description = "Autenticação necessária."),
        @ApiResponse(responseCode = "403",
                description = "Acesso negado.")
    })
    @GetMapping("/relatorios/resumo")
    public ResponseEntity<Map<String, BigDecimal>> resumoFinanceiro(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long usuarioId) {

        BigDecimal receitas = transacaoService.totalPorTipo(
                usuarioId,
                TipoTransacao.RECEITA
        );

        BigDecimal despesas = transacaoService.totalPorTipo(
                usuarioId,
                TipoTransacao.DESPESA
        );

        BigDecimal saldo = receitas.subtract(despesas);

        return ResponseEntity.ok(
                Map.of(
                        "receitas", receitas,
                        "despesas", despesas,
                        "saldo", saldo
                )
        );
    }
}
