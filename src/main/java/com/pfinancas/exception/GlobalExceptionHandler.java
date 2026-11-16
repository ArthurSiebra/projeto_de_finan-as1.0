
package com.pfinancas.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pfinancas.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ERRO 404 - RECURSO NAO ENCONTRADO
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> tratarNaoEncontrado(
            ResourceNotFoundException ex) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // ERRO 409 - RECURSO DUPLICADO
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponseDTO> tratarDuplicado(
            RecursoDuplicadoException ex) {

        return criarResposta(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    // ERRO 400 - SALDO INSUFICIENTE
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponseDTO> tratarSaldoInsuficiente(
            SaldoInsuficienteException ex) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // ERRO 400 - ARGUMENTO INVALIDO
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> tratarArgumentoInvalido(
            IllegalArgumentException ex) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // ERRO 400 - VALIDACAO DE DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> tratarValidacao(
            MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(erro -> erro.getDefaultMessage())
                .orElse("Dados inválidos.");

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                mensagem
        );
    }

    // PADRAO DE RESPOSTA
    private ResponseEntity<ErrorResponseDTO> criarResposta(
            HttpStatus status,
            String mensagem) {

        ErrorResponseDTO erro = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem
        );

        return ResponseEntity.status(status).body(erro);
    }


    // ERRO 401 - CREDENCIAIS INVALIDAS
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> tratarCredenciaisInvalidas(
            CredenciaisInvalidasException ex) {

        return criarResposta(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }

}
