package com.mahashakti.mahashaktiBE.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JwtService {

    private static final String SECRET = "FCF4FBF9CAFAA0BD050C4691FF5B21AE9CC8E3BA97B37E71E782F312687676AD3FB8F6AB7E38F5D966990703CBE00541A2DDBD4DAE8C56D697DBF5125472F3C9";
    private static final long VALIDITY = TimeUnit.DAYS.toMillis(60);

    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", "admin");

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        /*
            SecretKey key = Jwts.SIG.HS512.key().build();
            SECRET = DatatypeConverter.printHexBinary(key.getEncoded());
            og.info("secret: {}", SECRET);
        */
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String jwt) {
        return getClaims(jwt).getSubject();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean isTokenValid(String jwt) {
        return getClaims(jwt).getExpiration().after(Date.from(Instant.now()));
    }
}
