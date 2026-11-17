
package com.pfinancas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfinancas.dto.CadastroUsuarioDTO;
import com.pfinancas.dto.LoginDTO;
import com.pfinancas.dto.TokenDTO;
import com.pfinancas.servico.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Autenticação",
    description = "Cadastro de usuários e autenticação com JWT."
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // CADASTRO DE USUARIO
    @Operation(
        summary = "Cadastrar novo usuário",
        description = "Registra um usuário no sistema. "
                + "A senha é armazenada utilizando BCrypt."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuário cadastrado com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de cadastro inválidos."
        ),
        @ApiResponse(
            responseCode = "409",
            description = "E-mail já cadastrado."
        )
    })
    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrar(
            @Valid @RequestBody CadastroUsuarioDTO dto) {

        authService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // LOGIN DE USUARIO
    @Operation(
        summary = "Autenticar usuário",
        description = "Valida o e-mail e a senha do usuário "
                + "e retorna um token JWT para acessar "
                + "os endpoints protegidos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Autenticação realizada com sucesso."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de login inválidos."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "E-mail ou senha incorretos."
        )
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(
            @Valid @RequestBody LoginDTO dto) {

        TokenDTO token = authService.login(dto);

        return ResponseEntity.ok(token);
    }
}
