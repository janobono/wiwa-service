package sk.janobono.wiwa.common.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.common.config.VerificationConfigProperties;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class VerificationToken {

    private final Algorithm algorithm;
    private final String issuer;

    public VerificationToken(VerificationConfigProperties verificationConfigProperties) {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        this.issuer = verificationConfigProperties.issuer();
    }

    public String generateToken(Map<String, String> data, Long issuedAt, Long expiresAt) {
        try {
            JWTCreator.Builder jwtBuilder = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(new Date(issuedAt))
                    .withExpiresAt(new Date(expiresAt));
            data.forEach(jwtBuilder::withClaim);
            return jwtBuilder.sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DecodedJWT decodeToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    public Map<String, String> parseToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        Map<String, String> result = new HashMap<>();
        jwt.getClaims().forEach((key, value) -> result.put(key, value.asString()));
        return result;
    }
}
