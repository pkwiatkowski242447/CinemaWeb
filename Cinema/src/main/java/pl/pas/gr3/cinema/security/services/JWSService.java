package pl.pas.gr3.cinema.security.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.util.consts.model.MovieConstants;
import pl.pas.gr3.cinema.util.consts.model.TicketConstants;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Account;

@Service
public class JWSService {

    @Value("${security.jwt.token.secret-key:secret-value}")
    private String SECRET_KEY;

    public String generateSignatureForUser(Account account) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(UserConstants.USER_ID, account.getId().toString())
                .withClaim(UserConstants.USER_LOGIN, account.getLogin())
                .withClaim(UserConstants.USER_STATUS_ACTIVE, account.isActive())
                .sign(algorithm);
    }

    public String generateSignatureForMovie(Movie movie) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(MovieConstants.MOVIE_ID, movie.getId().toString())
                .sign(algorithm);
    }

    public String generateSignatureForTicket(Ticket ticket) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(TicketConstants.TICKET_ID, ticket.getId().toString())
                .withClaim(TicketConstants.TICKET_FINAL_PRICE, ticket.getPrice())
                .withClaim(TicketConstants.USER_ID, ticket.getUserId().toString())
                .withClaim(TicketConstants.MOVIE_ID, ticket.getMovieId().toString())
                .sign(algorithm);
    }

    public String extractUsernameFromSignature(String signature) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(signature);
        return decodedJWT.getClaim(UserConstants.USER_LOGIN).asString();
    }

    public boolean verifyUserSignature(String signature, Account account) {
        String currentSignature = this.generateSignatureForUser(account);
        return signature.equals(currentSignature);
    }

    public boolean verifyMovieSignature(String signature, Movie movie) {
        String currentSignature = this.generateSignatureForMovie(movie);
        return signature.equals(currentSignature);
    }

    public boolean verifyTicketSignature(String signature, Ticket ticket) {
        String currentSignature = this.generateSignatureForTicket(ticket);
        return signature.equals(currentSignature);
    }

    private String getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
