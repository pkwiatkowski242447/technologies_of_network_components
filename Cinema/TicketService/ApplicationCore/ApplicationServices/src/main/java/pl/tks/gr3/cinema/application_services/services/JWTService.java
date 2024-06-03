package pl.tks.gr3.cinema.application_services.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;

import java.util.*;


@Service
public class JWTService implements JWTUseCase {

    private final static String SECRET_KEY = "256970464e4d29792c4d2d24317256534d2d3039332a2b2e383929635f";


    @Override
    public String extractUsername(String jwtToken) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey())).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(jwtToken);
        return decodedJWT.getSubject();
    }

    @Override
    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey()))
                    .withSubject(userDetails.getUsername())
                    .build();
            jwtVerifier.verify(jwtToken);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    @Override
    public boolean isTokenValid(String jwtToken) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(getSignInKey()))
                    .build();
            jwtVerifier.verify(jwtToken);
            return JWT.decode(jwtToken).getExpiresAt().after(new Date());
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    private String getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new String(keyBytes);
    }
}
