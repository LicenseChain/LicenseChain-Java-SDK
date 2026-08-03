package com.licensechain;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * RS256 license_token verification via JWKS (parity with Node verifyLicenseAssertionJwt).
 */
public final class LicenseAssertion {

    /** Must match Core API LICENSE_TOKEN_USE_CLAIM. */
    public static final String LICENSE_TOKEN_USE_CLAIM = "licensechain_license_v1";

    public static final class VerifyLicenseAssertionOptions {
        private String expectedAppId;
        private String issuer;

        public String getExpectedAppId() {
            return expectedAppId;
        }

        public VerifyLicenseAssertionOptions setExpectedAppId(String expectedAppId) {
            this.expectedAppId = expectedAppId;
            return this;
        }

        public String getIssuer() {
            return issuer;
        }

        public VerifyLicenseAssertionOptions setIssuer(String issuer) {
            this.issuer = issuer;
            return this;
        }
    }

    /**
     * Verify a license_token using JWKS (RS256).
     */
    public static JWTClaimsSet verifyLicenseAssertionJwt(
            String token,
            String jwksUrl,
            VerifyLicenseAssertionOptions options
    ) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("empty token");
        }
        if (jwksUrl == null || jwksUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("empty jwksUrl");
        }
        token = token.trim();
        jwksUrl = jwksUrl.trim();

        SignedJWT signedJWT = SignedJWT.parse(token);
        if (!JWSAlgorithm.RS256.equals(signedJWT.getHeader().getAlgorithm())) {
            throw new SecurityException("expected RS256");
        }

        JWKSet jwkSet = JWKSet.load(new URL(jwksUrl));
        String kid = signedJWT.getHeader().getKeyID();
        JWK jwk = null;
        if (kid != null && !kid.isEmpty()) {
            jwk = jwkSet.getKeyByKeyId(kid);
        }
        if (jwk == null) {
            for (JWK k : jwkSet.getKeys()) {
                if ("RSA".equals(k.getKeyType().getValue())) {
                    jwk = k;
                    break;
                }
            }
        }
        if (!(jwk instanceof RSAKey)) {
            throw new SecurityException("no matching RSA JWK");
        }

        RSAKey rsaKey = (RSAKey) jwk;
        RSASSAVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
        if (!signedJWT.verify(verifier)) {
            throw new SecurityException("signature verification failed");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        Date now = new Date();
        if (claims.getExpirationTime() != null && claims.getExpirationTime().before(now)) {
            throw new SecurityException("token expired");
        }

        if (options != null && options.getIssuer() != null && !options.getIssuer().isEmpty()) {
            String iss = claims.getIssuer();
            if (iss == null || !options.getIssuer().equals(iss)) {
                throw new SecurityException("issuer mismatch");
            }
        }

        String tu = claims.getStringClaim("token_use");
        if (!LICENSE_TOKEN_USE_CLAIM.equals(tu)) {
            throw new SecurityException("Invalid license token: expected token_use \"" + LICENSE_TOKEN_USE_CLAIM + "\"");
        }

        if (options != null && options.getExpectedAppId() != null && !options.getExpectedAppId().trim().isEmpty()) {
            String want = options.getExpectedAppId().trim();
            List<String> aud = claims.getAudience();
            if (aud == null || aud.isEmpty() || !aud.contains(want)) {
                throw new SecurityException("Invalid license token: aud does not match expected app id");
            }
        }

        return claims;
    }
}
