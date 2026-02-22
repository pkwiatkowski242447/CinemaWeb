package pl.pas.gr3.cinema.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.dto.ObjectSignatureResponse;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;

import java.util.Base64;

@Service
public class JWSService {

    @Value("${security.jwt.token.secret-key:secret-value}")
    private String secretKey;

    public String generateSignature(ObjectSignatureResponse response) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT.create()
            .withPayload(response.obtainSigningFields())
            .sign(algorithm);
    }

    public String extractUsernameFromSignature(String signature) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(signature);
        return decodedJWT.getClaim(UserConstants.USER_LOGIN).asString();
    }

    private byte[] getSignInKey() {
        return Base64.getDecoder().decode(secretKey);
    }
}
