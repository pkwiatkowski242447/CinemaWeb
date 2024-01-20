package pl.pas.gr3.cinema.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.model.users.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTService {

    public UUID extractUserId(String jwtToken) {
        return UUID.fromString(extractClaim(jwtToken, Claims::getId));
    }

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public Date extractExpirationDate(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parser()
                .decryptWith(this.getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SecurityConstants.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJWTToken(Map<String, Object> extractClaims, User userDetails) {
        return Jwts
                .builder()
                .claims(extractClaims)
                .id(userDetails.getUserID().toString())
                .subject(userDetails.getUsername())
                .issuedAt(new Date(Instant.now().toEpochMilli()))
                .expiration(new Date(Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli()))
                .signWith(this.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJWTToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.USER_ROLE_CLAIM, userDetails.getUserRole());
        return generateJWTToken(claims, userDetails);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String userName = this.extractUsername(jwtToken);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
    }

    public boolean isTokenExpired(String jwtToken) {
        return extractExpirationDate(jwtToken).before(new Date());
    }
}
