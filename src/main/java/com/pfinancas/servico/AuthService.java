
package com.pfinancas.servico;

import com.pfinancas.dto.CadastroUsuarioDTO;
import com.pfinancas.dto.LoginDTO;
import com.pfinancas.dto.TokenDTO;

import com.pfinancas.exception.CredenciaisInvalidasException;
import com.pfinancas.exception.RecursoDuplicadoException;

import com.pfinancas.model.Usuario;
import com.pfinancas.repository.UsuarioRepository;
import com.pfinancas.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // CADASTRAR NOVO USUARIO
    @Transactional
    public void cadastrar(CadastroUsuarioDTO dto) {

        String email = dto.email().trim().toLowerCase();

        // Verifica se o email ja esta cadastrado
        if (usuarioRepository.existsByEmail(email)) {

            throw new RecursoDuplicadoException(
                    "Este e-mail já está cadastrado."
            );
        }

        // Criptografa a senha com BCrypt
        String senhaCriptografada =
                passwordEncoder.encode(dto.senha());

        // Cria o usuario
        Usuario usuario = new Usuario(
                dto.nome().trim(),
                email,
                senhaCriptografada
        );

        // Salva no banco de dados
        usuarioRepository.save(usuario);
    }

    // AUTENTICAR USUARIO
    @Transactional(readOnly = true)
    public TokenDTO login(LoginDTO dto) {

        String email = dto.email().trim().toLowerCase();

        // Busca o usuario pelo email
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new CredenciaisInvalidasException(
                                "E-mail ou senha inválidos."
                        )
                );

        // Verifica a senha utilizando BCrypt
        if (!passwordEncoder.matches(
                dto.senha(),
                usuario.getSenha())) {

            throw new CredenciaisInvalidasException(
                    "E-mail ou senha inválidos."
            );
        }

        // Gera o token JWT
        String token = jwtService.gerarToken(
                usuario.getId()
        );

        return new TokenDTO(token, "Bearer");
    }
}
