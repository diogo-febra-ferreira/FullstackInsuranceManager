package pt.ipleiria.estg.dei.ei.dae.project.security;

import io.jsonwebtoken.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class TokenIssuer {
    protected static final byte[] SECRET_KEY = "secret".getBytes();
    protected static final String ALGORITHM = "DES";
    public static final long EXPIRY_MINS = 60L;
    public String issue(String username) {
        LocalDateTime expiryPeriod = LocalDateTime.now().plusMinutes(EXPIRY_MINS);
        Date expirationDateTime = Date.from(
                expiryPeriod.atZone(ZoneId.systemDefault()).toInstant()
        );
        Key key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, key)
                .setIssuedAt(new Date())
                .setExpiration(expirationDateTime)
                .compact();
    }
/*
    public static boolean verify(String token) {
        Key key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static void invalidate(String token) {
        Key key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        long expirationMillis = claims.getBody().getExpiration().getTime();
        Date expirationDate = new Date(expirationMillis);
        expirationDate.setTime(0);
        claims.getBody().setExpiration(expirationDate);
    }*/
}
