
package com.pfinancas.controller;

import com.pfinancas.dto.SaldoDTO;
import com.pfinancas.dto.TransacaoResponseDTO;
import com.pfinancas.servico.TransacaoService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(
            TransacaoService transacaoService) {

        this.transacaoService = transacaoService;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<TransacaoResponseDTO> listar(
            @PathVariable Long usuarioId) {

        return transacaoService.listarPorUsuario(usuarioId)
                .stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/usuario/{usuarioId}/saldo")
    public SaldoDTO consultarSaldo(
            @PathVariable Long usuarioId) {

        return new SaldoDTO(
                transacaoService.calcularSaldo(usuarioId)
        );
    }
}
