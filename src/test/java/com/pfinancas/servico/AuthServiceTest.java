
package com.pfinancas.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pfinancas.dto.CadastroUsuarioDTO;
import com.pfinancas.dto.LoginDTO;
import com.pfinancas.dto.TokenDTO;
import com.pfinancas.exception.CredenciaisInvalidasException;
import com.pfinancas.exception.RecursoDuplicadoException;
import com.pfinancas.model.Usuario;
import com.pfinancas.repository.UsuarioRepository;
import com.pfinancas.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void configurar() {

        authService = new AuthService(
                usuarioRepository,
                passwordEncoder,
                jwtService
        );
    }

    // TESTE 1 - CADASTRAR USUARIO
    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {

        CadastroUsuarioDTO dto = new CadastroUsuarioDTO(
                "Arthur",
                "ARTHUR@EMAIL.COM",
                "senha12345"
        );

        when(usuarioRepository.existsByEmail("arthur@email.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("senha12345"))
                .thenReturn("hash-bcrypt");

        authService.cadastrar(dto);

        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getNome().equals("Arthur")
                && usuario.getEmail().equals("arthur@email.com")
                && usuario.getSenha().equals("hash-bcrypt")
        ));
    }

    // TESTE 2 - REJEITAR EMAIL DUPLICADO
    @Test
    void deveRejeitarEmailDuplicado() {

        CadastroUsuarioDTO dto = new CadastroUsuarioDTO(
                "Arthur",
                "arthur@email.com",
                "senha12345"
        );

        when(usuarioRepository.existsByEmail("arthur@email.com"))
                .thenReturn(true);

        assertThrows(
                RecursoDuplicadoException.class,
                () -> authService.cadastrar(dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    // TESTE 3 - LOGIN COM SENHA CORRETA
    @Test
    void deveRealizarLoginComSenhaCorreta() {

        Usuario usuario = new Usuario(
                "Arthur",
                "arthur@email.com",
                "hash-bcrypt"
        );

        usuario.setId(1L);

        when(usuarioRepository.findByEmail("arthur@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("senha12345", "hash-bcrypt"))
                .thenReturn(true);

        when(jwtService.gerarToken(1L))
                .thenReturn("token-de-teste");

        TokenDTO resposta = authService.login(
                new LoginDTO(
                        "arthur@email.com",
                        "senha12345"
                )
        );

        assertEquals("token-de-teste", resposta.token());
        assertEquals("Bearer", resposta.tipo());
    }

    // TESTE 4 - REJEITAR SENHA INCORRETA
    @Test
    void deveRejeitarSenhaIncorreta() {

        Usuario usuario = new Usuario(
                "Arthur",
                "arthur@email.com",
                "hash-bcrypt"
        );

        when(usuarioRepository.findByEmail("arthur@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("senha-errada", "hash-bcrypt"))
                .thenReturn(false);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(
                        new LoginDTO(
                                "arthur@email.com",
                                "senha-errada"
                        )
                )
        );

        verifyNoInteractions(jwtService);
    }
}
