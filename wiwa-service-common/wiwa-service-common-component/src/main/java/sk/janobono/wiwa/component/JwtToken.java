package sk.janobono.wiwa.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.model.User;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtToken {

    private static final String ID = "id";

    private final Algorithm algorithm;
    private final Long expiration;
    private final String issuer;

    public JwtToken(final JwtConfigProperties jwtConfigProperties) {
        final KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(1024);
        final KeyPair keyPair = keyGen.generateKeyPair();
        this.algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        this.expiration = TimeUnit.MINUTES.toMillis(jwtConfigProperties.expiration());
        this.issuer = jwtConfigProperties.issuer();
    }

    public Long expiresAt(final Long issuedAt) {
        return issuedAt + expiration;
    }

    public String generateToken(final User user, final Long issuedAt) {
        try {
            final JWTCreator.Builder jwtBuilder = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(new Date(issuedAt))
                    .withExpiresAt(new Date(expiresAt(issuedAt)));
            jwtBuilder.withClaim(ID, user.id());
            return jwtBuilder.sign(algorithm);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DecodedJWT decodeToken(final String token) throws JWTVerificationException {
        final JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

    public Long parseToken(final String token) {
        final DecodedJWT jwt = decodeToken(token);
        return jwt.getClaims().get(ID).asLong();
    }
}
