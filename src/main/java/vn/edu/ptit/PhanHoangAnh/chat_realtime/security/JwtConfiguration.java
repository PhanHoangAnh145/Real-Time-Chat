package vn.edu.ptit.PhanHoangAnh.chat_realtime.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dao.UserRepository;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ExchangeTokenResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.LoginResponseDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.RefreshToken;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.RefreshTokenService;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtConfiguration {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    private final RefreshTokenService refreshTokenService;
    private final JwtEncoder jwtEncoder;

    @Value("${chat-realtime.access-token-validation-in-seconds}")
    private Long accessTokenExpiration;

    @Value("${chat-realtime.refresh-token-validation-in-seconds}")
    private Long refreshTokenExpiration;

    public String getScope(Authentication authentication) {
        if (authentication != null) {
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            return scope;
        }
        return "UNKNOWN";
    }

    //tao refresh token
    public String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);
        String token = generateSecureToken();

        RefreshToken rf = new RefreshToken();
        rf.setCreatedAt(now);
        rf.setExpiredAt(validity);
        rf.setToken(token);
        rf.setUser(user);
        this.refreshTokenService.createRefreshToken(rf);
        return token;
    }

    //tao access token
    public String createAccessToken(Authentication authentication, Long userId) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        String scope = getScope(authentication);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("id", userId)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public ExchangeTokenResponse handleExchangeToken(String inputToken) {
        //check coi token het han chua
        RefreshToken refreshToken = this.refreshTokenService.findRefreshTokenByToken(inputToken);
        Instant now = Instant.now();
        if (now.isAfter(refreshToken.getExpiredAt())) {
            throw new RuntimeException("Refresh het han");
        }

        User currentUser = refreshToken.getUser();
        String newRefreshToken = this.createRefreshToken(currentUser);
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        String scope  = currentUser.getRoles().stream().map(role -> "ROLE_" + role.getName())
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(currentUser.getUsername())
                .claim("scope", scope)
                .claim("id", currentUser.getId())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        ExchangeTokenResponse exToken = new ExchangeTokenResponse();
        exToken.setRefreshToken(newRefreshToken);
        exToken.setAccessToken(accessToken);
        exToken.setUser(new LoginResponseDTO.UserLogin(currentUser.getId(), currentUser.getUsername(), scope));

        this.refreshTokenService.deleteRefreshTokenById(refreshToken.getId());
        return exToken;
    }
}
