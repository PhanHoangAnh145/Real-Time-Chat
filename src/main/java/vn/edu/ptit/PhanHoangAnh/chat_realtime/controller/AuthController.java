package vn.edu.ptit.PhanHoangAnh.chat_realtime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ExchangeTokenResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.LoginRequestDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.LoginResponseDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.RefreshToken;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.ApiResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.security.JwtConfiguration;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.RefreshTokenService;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtConfiguration jwtConfiguration;
    private final RefreshTokenService refreshTokenService;

    @Value("${chat-realtime.refresh-token-validation-in-seconds}")
    private Long refreshTokenExpiration;

    @Value("${chat-realtime.security.jwt.base64-secret}")
    private String name;
    @PostMapping("/auth/login")
    public ResponseEntity<?> postLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        User currentUser = this.userService.findUserByUsername(authentication.getName());
        String accessToken = this.jwtConfiguration.createAccessToken(authentication, currentUser.getId());
        String refreshToken = this.jwtConfiguration.createRefreshToken(currentUser);
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setAccessToken(accessToken);
        responseDTO.setRefreshToken(refreshToken);
        String scope = this.jwtConfiguration.getScope(authentication);
        responseDTO.setUser(new LoginResponseDTO.UserLogin(
                currentUser.getId(),
                authentication.getName(),
                scope
                )
        );

        ResponseCookie resCookies = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("Lax")
                .build();
        ApiResponse<LoginResponseDTO> finalData = new ApiResponse<>(HttpStatus.OK, "", responseDTO, "");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(finalData);
    }

    @PostMapping("/auth/refresh")
    @Transactional
    public ResponseEntity<?> postRefreshToken(@RequestParam("token") String refreshToken) {
        ExchangeTokenResponse exchangeTokenResponse = this.jwtConfiguration.handleExchangeToken(refreshToken);
        return ApiResponse.success(exchangeTokenResponse);
    }

    @PostMapping("/auth/refresh-with-cookie")
    @Transactional
    public ResponseEntity<?> postRefreshTokenWithCookie(@CookieValue(required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Không tìm thấy Refresh Token trong Cookie!");
        }

        ExchangeTokenResponse exchangeTokenResponse = this.jwtConfiguration.handleExchangeToken(refreshToken);


        ResponseCookie resCookies = ResponseCookie
                .from("refreshToken", exchangeTokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("Lax")
                .build();


        ApiResponse<ExchangeTokenResponse> finalData = new ApiResponse<>(HttpStatus.OK, "Làm mới token thành công", exchangeTokenResponse, "");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(finalData);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<?> getAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();

        String userId = jwt.getClaimAsString("id");
        String username = jwt.getSubject();
        String role = jwt.getClaimAsString("scope");

        LoginResponseDTO.UserLogin user = new LoginResponseDTO.UserLogin();
        user.setId(Long.valueOf(userId));
        user.setUsername(username);
        user.setRole(role);

        return ApiResponse.success(user);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> postLogout(@AuthenticationPrincipal Jwt jwt,
                                        @CookieValue(required = false) String refreshToken) {
        String userId = jwt.getClaimAsString("id");
        String username = jwt.getSubject();

        RefreshToken currentTokenInDB = this.refreshTokenService.findRefreshTokenByToken(refreshToken);
        this.refreshTokenService.deleteRefreshTokenById(currentTokenInDB.getId());

        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refreshToken", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ApiResponse<String> finalData = new ApiResponse<>(HttpStatus.OK, "", "ok", "");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(finalData);
    }

}
