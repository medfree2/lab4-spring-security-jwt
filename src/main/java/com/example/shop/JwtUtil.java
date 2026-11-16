package com.example.shop;

import java.time.Instant;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
class JwtUtil {
    static final String ISSUER = "lab4-shop";
    private final JwtEncoder encoder;

    JwtUtil(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    String issue(UserDetails user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(ISSUER).subject(user.getUsername())
                .issuedAt(now).expiresAt(now.plusSeconds(900)).build();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
