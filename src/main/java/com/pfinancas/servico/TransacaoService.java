
package com.pfinancas.servico;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pfinancas.dto.TransacaoDTO;
import com.pfinancas.dto.TransacaoResponseDTO;
import com.pfinancas.exception.ResourceNotFoundException;
import com.pfinancas.exception.SaldoInsuficienteException;
import com.pfinancas.model.Categoria;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Transacao;
import com.pfinancas.model.Usuario;
import com.pfinancas.repository.CategoriaRepository;
import com.pfinancas.repository.TransacaoRepository;
import com.pfinancas.repository.UsuarioRepository;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public TransacaoService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository) {

        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // LISTAR TRANSACOES
    @Transactional(readOnly = true)
    public List<Transacao> listarPorUsuario(Long usuarioId) {

        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    // LISTAR TRANSACOES EM DTO
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarPorUsuarioDTO(
            Long usuarioId) {

        return transacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    // CALCULAR SALDO
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldo(Long usuarioId) {

        List<Transacao> transacoes =
                transacaoRepository.findByUsuarioId(usuarioId);

        BigDecimal saldo = BigDecimal.ZERO;

        for (Transacao transacao : transacoes) {

            if (transacao.getTipo() == TipoTransacao.RECEITA) {
                saldo = saldo.add(transacao.getValor());

            } else if (transacao.getTipo() == TipoTransacao.DESPESA) {
                saldo = saldo.subtract(transacao.getValor());
            }
        }

        return saldo;
    }

    // CADASTRAR TRANSACAO
    @Transactional
    public Transacao cadastrarTransacao(
            Long usuarioId,
            Long categoriaId,
            Transacao transacao) {

        if (usuarioId == null ||
                categoriaId == null ||
                transacao == null) {

            throw new IllegalArgumentException(
                    "Usuário, categoria e transação são obrigatórios."
            );
        }

        if (transacao.getValor() == null ||
                transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "O valor da transação deve ser maior que zero."
            );
        }

        if (transacao.getTipo() == null) {
            throw new IllegalArgumentException(
                    "O tipo da transação é obrigatório."
            );
        }

        if (transacao.getDescricao() == null ||
                transacao.getDescricao().isBlank()) {

            throw new IllegalArgumentException(
                    "A descrição da transação é obrigatória."
            );
        }

        if (transacao.getData() == null) {
            throw new IllegalArgumentException(
                    "A data da transação é obrigatória."
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado."
                ));

        Categoria categoria = categoriaRepository
                .findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada para este usuário."
                ));

        if (transacao.getTipo() == TipoTransacao.DESPESA) {

            BigDecimal saldoAtual = calcularSaldo(usuarioId);

            if (transacao.getValor().compareTo(saldoAtual) > 0) {

                throw new SaldoInsuficienteException(
                        "Saldo insuficiente para realizar a despesa."
                );
            }
        }

        transacao.setId((Long) null);
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        return transacaoRepository.save(transacao);
    }

    // CADASTRAR E RETORNAR DTO
    @Transactional
    public TransacaoResponseDTO cadastrarTransacaoDTO(
            Long usuarioId,
            Long categoriaId,
            Transacao transacao) {

        Transacao salva = cadastrarTransacao(
                usuarioId,
                categoriaId,
                transacao
        );

        return TransacaoResponseDTO.fromEntity(salva);
    }

    // ATUALIZAR TRANSACAO
    @Transactional
    public TransacaoResponseDTO atualizarTransacao(
            Long usuarioId,
            Long transacaoId,
            TransacaoDTO dto) {

        if (usuarioId == null ||
                transacaoId == null ||
                dto == null) {

            throw new IllegalArgumentException(
                    "Usuário, transação e dados são obrigatórios."
            );
        }

        if (dto.categoriaId() == null ||
                dto.valor() == null ||
                dto.valor().compareTo(BigDecimal.ZERO) <= 0 ||
                dto.tipo() == null ||
                dto.descricao() == null ||
                dto.descricao().isBlank() ||
                dto.data() == null) {

            throw new IllegalArgumentException(
                    "Dados da transação inválidos."
            );
        }

        Transacao transacao = transacaoRepository
                .findByIdAndUsuarioId(transacaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transação não encontrada para este usuário."
                ));

        Categoria categoria = categoriaRepository
                .findByIdAndUsuarioId(dto.categoriaId(), usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria não encontrada para este usuário."
                ));

        BigDecimal saldoAtual = calcularSaldo(usuarioId);

        BigDecimal saldoSemTransacao;

        if (transacao.getTipo() == TipoTransacao.RECEITA) {

            saldoSemTransacao = saldoAtual.subtract(
                    transacao.getValor()
            );

        } else {

            saldoSemTransacao = saldoAtual.add(
                    transacao.getValor()
            );
        }

        BigDecimal saldoFinal;

        if (dto.tipo() == TipoTransacao.RECEITA) {

            saldoFinal = saldoSemTransacao.add(dto.valor());

        } else {

            saldoFinal = saldoSemTransacao.subtract(dto.valor());
        }

        if (saldoFinal.compareTo(BigDecimal.ZERO) < 0) {

            throw new SaldoInsuficienteException(
                    "A atualização deixaria o saldo negativo."
            );
        }

        transacao.setDescricao(dto.descricao());
        transacao.setValor(dto.valor());
        transacao.setTipo(dto.tipo());
        transacao.setData(dto.data());
        transacao.setCategoria(categoria);

        Transacao atualizada =
                transacaoRepository.save(transacao);

        return TransacaoResponseDTO.fromEntity(atualizada);
    }

    // EXCLUIR TRANSACAO
    @Transactional
    public void excluirTransacao(
            Long usuarioId,
            Long transacaoId) {

        if (usuarioId == null || transacaoId == null) {

            throw new IllegalArgumentException(
                    "Usuário e transação são obrigatórios."
            );
        }

        Transacao transacao = transacaoRepository
                .findByIdAndUsuarioId(transacaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transação não encontrada para este usuário."
                ));

        if (transacao.getTipo() == TipoTransacao.RECEITA) {

            BigDecimal saldoAtual = calcularSaldo(usuarioId);

            BigDecimal saldoAposExclusao =
                    saldoAtual.subtract(transacao.getValor());

            if (saldoAposExclusao.compareTo(BigDecimal.ZERO) < 0) {

                throw new SaldoInsuficienteException(
                        "Não é possível excluir esta receita: "
                        + "o saldo ficaria negativo."
                );
            }
        }

        transacaoRepository.delete(transacao);
    }

    // RELATORIO POR PERIODO
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> relatorioPorPeriodo(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim) {

        if (inicio == null ||
                fim == null ||
                inicio.isAfter(fim)) {

            throw new IllegalArgumentException(
                    "O período informado é inválido."
            );
        }

        return transacaoRepository
                .buscarPorPeriodo(usuarioId, inicio, fim)
                .stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    // RELATORIO POR CATEGORIA
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> relatorioPorCategoria(
            Long usuarioId,
            Long categoriaId) {

        if (categoriaId == null) {

            throw new IllegalArgumentException(
                    "A categoria é obrigatória."
            );
        }

        return transacaoRepository
                .buscarPorCategoria(usuarioId, categoriaId)
                .stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    // RELATORIO POR TIPO
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> relatorioPorTipo(
            Long usuarioId,
            TipoTransacao tipo) {

        if (tipo == null) {

            throw new IllegalArgumentException(
                    "O tipo da transação é obrigatório."
            );
        }

        return transacaoRepository
                .buscarPorTipo(usuarioId, tipo)
                .stream()
                .map(TransacaoResponseDTO::fromEntity)
                .toList();
    }

    // SOMAR RECEITAS OU DESPESAS
    @Transactional(readOnly = true)
    public BigDecimal totalPorTipo(
            Long usuarioId,
            TipoTransacao tipo) {

        if (tipo == null) {

            throw new IllegalArgumentException(
                    "O tipo da transação é obrigatório."
            );
        }

        return transacaoRepository.somarPorTipo(
                usuarioId,
                tipo
        );
    }
}
