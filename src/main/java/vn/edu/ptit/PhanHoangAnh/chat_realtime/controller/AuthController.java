package vn.edu.ptit.PhanHoangAnh.chat_realtime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

        String accessToken = this.jwtConfiguration.createAccessToken(authentication, 1L);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setAccessToken(accessToken);
        String scope = this.jwtConfiguration.getScope(authentication);
        responseDTO.setUser(new LoginResponseDTO.UserLogin(
                1L,
                authentication.getName(),
                scope
        ));

        return ApiResponse.success(responseDTO);
    }
}
