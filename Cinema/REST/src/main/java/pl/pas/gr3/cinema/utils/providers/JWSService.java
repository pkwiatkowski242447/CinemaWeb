package pl.pas.gr3.cinema.utils.providers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.utils.messages.AccountConstants;

import java.util.Base64;

@Service
public class JWSService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    public String extractUsernameFromSignature(String signature) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(signature);
        return decodedJWT.getClaim(AccountConstants.ACCOUNT_LOGIN).asString();
    }

    private String getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
