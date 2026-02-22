package pl.pas.gr3.cinema.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JWTService {

    @Value("${security.jwt.token.secret-key:secret-value}")
    private String secretKey;

    public String extractLogin(String jwtToken) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(jwtToken);
        return decodedJWT.getSubject();
    }

    public String generateJWTToken(UserDetails userDetails) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        List<String> listOfRoles = new ArrayList<>();

        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            listOfRoles.add(grantedAuthority.toString());
        }

        return JWT
                .create()
                .withSubject(userDetails.getUsername())
                .withClaim(UserConstants.USER_ROLE, listOfRoles)
                .withIssuedAt(new Date(Instant.now().toEpochMilli()))
                .withExpiresAt(new Date(Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli()))
                .sign(algorithm);
    }

    public String generateJWTToken(Account account) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                account.getLogin(),
                account.getPassword(),
                account.isActive(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(account.getRole().name()))
        );

        return generateJWTToken(userDetails);
    }

    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey()))
                    .withSubject(userDetails.getUsername())
                    .build();
            DecodedJWT decodedJWT = jwtVerifier.verify(jwtToken);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private byte[] getSignInKey() {
        return Base64.getDecoder().decode(this.secretKey);
    }
}
