package com.scheduler.apigateway.security.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import static io.jsonwebtoken.io.Decoders.BASE64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret_key}")
    private String secretKey;

    private static SecretKey signingKey;

    @PostConstruct
    public void createSigningKey() {
        byte[] keyBytes = BASE64.decode(secretKey);
        signingKey =  Keys.hmacShaKeyFor(keyBytes);
    }

    @Operation(summary = "토큰 인증")
    public void verifyToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);

        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            throw new JwtException("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Invalid token signature.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("Invalid JWT token.");
        }
    }

    @Operation(summary = "만료 확인")
    public void isExpired(String token) {
        getPayload(token).getExpiration();
    }

    @Operation(summary = "카테고리 확인")
    public String getCategory(String token) {
        return getPayload(token).get("category", String.class);
    }

    private Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
