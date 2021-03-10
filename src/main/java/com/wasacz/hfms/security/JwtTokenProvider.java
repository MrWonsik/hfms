package com.wasacz.hfms.security;

import com.wasacz.hfms.persistence.User;
import io.jsonwebtoken.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();


        return Jwts.builder()
                .setSubject("hfms-jwt")
                .claim("userInfo", getUserInfo(userPrincipal.getUser()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    Long getUserIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        Map<String, String> userInfoMap = claims.get("userInfo", Map.class);
        String userId = userInfoMap.get("id");
        return Long.parseLong(userId);
    }

    private UserInfo getUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId().toString())
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
    }

    boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty.");
        }
        return false;
    }

    @Builder
    @Getter
    private static final class UserInfo {
        private final String id;
        private final String role;
        private final String username;
    }
}
