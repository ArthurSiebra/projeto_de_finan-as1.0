
package com.pfinancas.servico;

import com.pfinancas.exception.SaldoInsuficienteException;
import com.pfinancas.model.Categoria;
import com.pfinancas.model.Transacao;
import com.pfinancas.model.TipoTransacao;
import com.pfinancas.model.Usuario;
import com.pfinancas.repository.CategoriaRepository;
import com.pfinancas.repository.TransacaoRepository;
import com.pfinancas.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<Transacao> listarPorUsuario(Long usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

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

    @Transactional
    public Transacao cadastrarTransacao(
            Long usuarioId,
            Long categoriaId,
            Transacao transacao) {

        if (usuarioId == null || categoriaId == null || transacao == null) {
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
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário não encontrado."
                ));

        Categoria categoria = categoriaRepository
                .findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
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
}
