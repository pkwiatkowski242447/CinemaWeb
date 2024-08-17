package pl.pas.gr3.cinema.utils.providers;

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
import pl.pas.gr3.cinema.model.users.AccessLevel;
import pl.pas.gr3.cinema.model.users.Account;
import pl.pas.gr3.cinema.utils.messages.AccountConstants;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JWTService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public String extractUsername(String jwtToken) {
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
                .withClaim(SecurityConstants.ACCESS_LEVELS, listOfRoles)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .withIssuer(SecurityConstants.ISSUER_CINEMA)
                .sign(algorithm);
    }

    public String generateJWTToken(Account account) {
        List<GrantedAuthority> listOfAuthorities = new ArrayList<>();
        for (AccessLevel accessLevel : account.getAccessLevels()) {
            listOfAuthorities.add(new SimpleGrantedAuthority("ROLE_" + accessLevel.getClass().getSimpleName().toUpperCase()));
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                account.getLogin(),
                account.getPassword(),
                account.getActive(),
                true,
                true,
                !account.getBlocked(),
                listOfAuthorities
        );

        return generateJWTToken(userDetails);
    }

    public boolean isTokenValid(String jwtToken) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey()))
                    .withClaimPresence("sub")
                    .withIssuer(SecurityConstants.ISSUER_CINEMA)
                    .build();
            jwtVerifier.verify(jwtToken);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private String getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
