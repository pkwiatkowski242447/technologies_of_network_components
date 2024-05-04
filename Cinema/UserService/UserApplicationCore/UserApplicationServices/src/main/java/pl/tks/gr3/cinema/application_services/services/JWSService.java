package pl.tks.gr3.cinema.application_services.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.adapters.consts.model.UserEntConstants;
import pl.tks.gr3.cinema.domain_model.User;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;

import java.util.Base64;

@Service(value = "JWSService")
public class JWSService implements JWSUseCase {

    private final static String SECRET_KEY = "256970464e4d29792c4d2d24317256534d2d3039332a2b2e383929635f";

    @Override
    public String generateSignatureForUser(User user) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        return JWT
                .create()
                .withClaim(UserEntConstants.USER_ID, user.getUserID().toString())
                .withClaim(UserEntConstants.USER_LOGIN, user.getUserLogin())
                .withClaim(UserEntConstants.USER_STATUS_ACTIVE, user.isUserStatusActive())
                .sign(algorithm);
    }

    @Override
    public String extractUsernameFromSignature(String signature) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(signature);
        return decodedJWT.getClaim(UserEntConstants.USER_LOGIN).asString();
    }

    @Override
    public boolean verifyUserSignature(String signature, User user) {
        String currentSignature = this.generateSignatureForUser(user);
        return signature.equals(currentSignature);
    }

    private String getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
