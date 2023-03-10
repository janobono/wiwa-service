package sk.janobono.wiwa.common.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sk.janobono.wiwa.common.config.JwtConfigProperties;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.common.model.UserSo;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtToken {

    private static final String ID = "id";
    private static final String TITLE_BEFORE = "titleBefore";
    private static final String FIRST_NAME = "firstName";
    private static final String MID_NAME = "midName";
    private static final String LAST_NAME = "lastName";
    private static final String TITLE_AFTER = "titleAfter";
    private static final String EMAIL = "email";
    private static final String GDPR = "gdpr";
    private static final String CONFIRMED = "confirmed";
    private static final String ENABLED = "enabled";

    private final Algorithm algorithm;
    private final Long expiration;
    private final String issuer;

    public JwtToken(JwtConfigProperties jwtConfigProperties) {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.algorithm = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        this.expiration = TimeUnit.HOURS.toMillis(jwtConfigProperties.expiration());
        this.issuer = jwtConfigProperties.issuer();
    }

    public Long expiresAt(Long issuedAt) {
        return issuedAt + expiration;
    }

    public String generateToken(UserSo user, Long issuedAt) {
        try {
            JWTCreator.Builder jwtBuilder = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(new Date(issuedAt))
                    .withExpiresAt(new Date(expiresAt(issuedAt)));
            // id
            jwtBuilder.withClaim(ID, user.id());
            // username
            jwtBuilder.withSubject(user.username());
            // titleBefore
            jwtBuilder.withClaim(TITLE_BEFORE, user.titleBefore());
            // firstName
            jwtBuilder.withClaim(FIRST_NAME, user.firstName());
            // midName
            jwtBuilder.withClaim(MID_NAME, user.midName());
            // lastName
            jwtBuilder.withClaim(LAST_NAME, user.lastName());
            // titleAfter
            jwtBuilder.withClaim(TITLE_AFTER, user.titleAfter());
            // email
            jwtBuilder.withClaim(EMAIL, user.email());
            // gdpr
            jwtBuilder.withClaim(GDPR, user.gdpr());
            // confirmed
            jwtBuilder.withClaim(CONFIRMED, user.confirmed());
            // enabled
            jwtBuilder.withClaim(ENABLED, user.enabled());
            // authorities
            if (Objects.nonNull(user.authorities())) {
                jwtBuilder.withAudience(
                        user.authorities().stream().map(Authority::toString).toArray(String[]::new)
                );
            }
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

    public UserSo parseToken(String token) {
        DecodedJWT jwt = decodeToken(token);
        // id
        Long id = jwt.getClaims().get(ID).asLong();
        // username
        String username = jwt.getSubject();
        // titleBefore
        String titleBefore = jwt.getClaims().containsKey(TITLE_BEFORE) ? jwt.getClaims().get(TITLE_BEFORE).asString() : null;
        // firstName
        String firstName = jwt.getClaims().get(FIRST_NAME).asString();
        // midName
        String midName = jwt.getClaims().containsKey(MID_NAME) ? jwt.getClaims().get(MID_NAME).asString() : null;
        // lastName
        String lastName = jwt.getClaims().get(LAST_NAME).asString();
        // titleAfter
        String titleAfter = jwt.getClaims().containsKey(TITLE_AFTER) ? jwt.getClaims().get(TITLE_AFTER).asString() : null;
        // email
        String email = jwt.getClaims().get(EMAIL).asString();
        // gdpr
        Boolean gdpr = jwt.getClaims().get(GDPR).asBoolean();
        // confirmed
        Boolean confirmed = jwt.getClaims().get(CONFIRMED).asBoolean();
        // enabled
        Boolean enabled = jwt.getClaims().get(ENABLED).asBoolean();
        // authorities
        List<String> authorities = jwt.getAudience();

        return new UserSo(
                id,
                username,
                titleBefore,
                firstName,
                midName,
                lastName,
                titleAfter,
                email,
                gdpr,
                confirmed,
                enabled,
                CollectionUtils.isEmpty(authorities) ? new HashSet<>() : authorities.stream().map(Authority::byValue).collect(Collectors.toSet())
        );
    }
}
