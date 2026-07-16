package vn.edu.ptit.PhanHoangAnh.chat_realtime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.ExchangeTokenResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.LoginRequestDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.dto.LoginResponseDTO;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.entity.User;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.ApiResponse;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.security.JwtConfiguration;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtConfiguration jwtConfiguration;

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

        return ApiResponse.success(responseDTO);
    }

    @PostMapping("/auth/refresh")
    @Transactional
    public ResponseEntity<?> postRefreshToken(@RequestParam("token") String refreshToken) {
        ExchangeTokenResponse exchangeTokenResponse = this.jwtConfiguration.handleExchangeToken(refreshToken);
        return ApiResponse.success(exchangeTokenResponse);
    }
}
