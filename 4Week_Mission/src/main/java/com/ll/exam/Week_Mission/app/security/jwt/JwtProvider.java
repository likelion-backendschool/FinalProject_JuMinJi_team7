package com.ll.exam.Week_Mission.app.security.jwt;

import com.ll.exam.Week_Mission.util.Ut;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final SecretKey jwtSecretKey;
    private long tokenValidTime = 30 * 60 * 1000L; // 30분 // 30 * 60 * 1000 * 1 ms

    private SecretKey getSecretKey() {
        return jwtSecretKey;
    }

    public String generateAccessToken(Map<String, Object> claims) {
        Date now = new Date();

        return Jwts.builder()
                .claim("body", Ut.json.toStr(claims)) // JWT payload에 저장되는 정보 단위
                .setIssuedAt(now) // 토큰 발행시간
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 토큰 만료시간
                .compact();
    }

    /* 토큰 유효성 검증 */
    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJwt(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /* 토큰 claims -> map */
    public Map<String, Object> getClaims(String token) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("body", String.class);

        return Ut.json.toMap(body);
    }
}
