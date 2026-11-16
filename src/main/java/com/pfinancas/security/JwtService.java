
package com.pfinancas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracao;

    public JwtService(
            @Value("${jwt.secret}") String segredo,
            @Value("${jwt.expiration-ms:3600000}") long expiracao) {

        byte[] chaveBytes = Base64.getDecoder().decode(segredo);

        if (chaveBytes.length < 32) {
            throw new IllegalArgumentException(
                    "A chave JWT deve ter pelo menos 256 bits."
            );
        }

        this.chave = Keys.hmacShaKeyFor(chaveBytes);
        this.expiracao = expiracao;
    }

    public String gerarToken(Long usuarioId) {

        Date agora = new Date();

        Date vencimento = new Date(
                agora.getTime() + expiracao
        );

        return Jwts.builder()
                .subject(usuarioId.toString())
                .issuedAt(agora)
                .expiration(vencimento)
                .signWith(chave)
                .compact();
    }

    public Long extrairUsuarioId(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public boolean validarToken(String token) {

        try {
            extrairUsuarioId(token);
            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
