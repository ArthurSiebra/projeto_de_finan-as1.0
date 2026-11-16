
package com.pfinancas.servico;

import com.pfinancas.dto.CategoriaDTO;
import com.pfinancas.dto.CategoriaResponseDTO;
import com.pfinancas.model.Categoria;
import com.pfinancas.model.Usuario;
import com.pfinancas.repository.CategoriaRepository;
import com.pfinancas.repository.UsuarioRepository;
import com.pfinancas.dto.CategoriaResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository) {

        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Categoria cadastrar(
            Long usuarioId,
            CategoriaDTO dto) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));

        Categoria categoria = new Categoria(
                dto.nome().trim(),
                usuario
        );

        return categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar(Long usuarioId) {

        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public CategoriaResponseDTO cadastrarDTO(
            Long usuarioId,
            CategoriaDTO dto) {

        Categoria categoria = cadastrar(usuarioId, dto);

        return CategoriaResponseDTO.fromEntity(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarDTO(
            Long usuarioId) {

        return categoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(CategoriaResponseDTO::fromEntity)
                .toList();
    }
}
