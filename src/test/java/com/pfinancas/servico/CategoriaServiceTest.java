
package com.pfinancas.servico;

import com.pfinancas.dto.CategoriaDTO;
import com.pfinancas.dto.CategoriaResponseDTO;
import com.pfinancas.model.Categoria;
import com.pfinancas.model.Usuario;
import com.pfinancas.repository.CategoriaRepository;
import com.pfinancas.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Usuario usuario;

    @BeforeEach
    void preparar() {
        usuario = new Usuario(
                "Arthur",
                "arthur@example.com",
                "senhaCriptografada"
        );

        usuario.setId(1L);
    }

    @Test
    void deveCadastrarCategoriaParaUsuarioCorreto() {

        CategoriaDTO dto = new CategoriaDTO("Alimentação");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Categoria categoria = categoriaService.cadastrar(1L, dto);

        assertEquals("Alimentação", categoria.getNome());
        assertSame(usuario, categoria.getUsuario());

        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void deveListarSomenteCategoriasDoUsuario() {

        Categoria categoria = new Categoria("Alimentação", usuario);

        when(categoriaRepository.findByUsuarioId(1L))
                .thenReturn(List.of(categoria));

        List<CategoriaResponseDTO> resultado =
                categoriaService.listarDTO(1L);

        assertEquals(1, resultado.size());
        assertEquals("Alimentação", resultado.get(0).nome());

        verify(categoriaRepository).findByUsuarioId(1L);
        verify(categoriaRepository, never()).findAll();
    }

    @Test
    void deveRejeitarCadastroParaUsuarioInexistente() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.cadastrar(
                        99L,
                        new CategoriaDTO("Transporte")
                )
        );

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }
}
