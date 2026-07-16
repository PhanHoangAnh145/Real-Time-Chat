package vn.edu.ptit.PhanHoangAnh.chat_realtime.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.exception.CustomAccessDeniedHandler;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.helper.exception.CustomAuthenticationEntryPoint;
import vn.edu.ptit.PhanHoangAnh.chat_realtime.service.CustomUserDetailsService;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${chat-realtime.security.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = jwtKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtConfiguration.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(JwtConfiguration.JWT_ALGORITHM).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        scopeConverter.setAuthoritiesClaimName("scope");
        scopeConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(scopeConverter);

        return converter;
    }

    @Bean
    public AuthenticationManager authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity,
                                                    CustomAccessDeniedHandler customAccessDeniedHandler,
                                                    CustomAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/refresh").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(login -> login.disable())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }
}
