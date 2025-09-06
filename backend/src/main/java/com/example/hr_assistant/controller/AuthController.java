package com.example.hr_assistant.controller;

import com.example.hr_assistant.model.User;
import com.example.hr_assistant.model.dto.LoginRequest;
import com.example.hr_assistant.model.dto.LoginResponse;
import com.example.hr_assistant.repository.UserRepository;
import com.example.hr_assistant.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для аутентификации
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "API для входа в систему")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя и получение JWT токена")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Аутентификация
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Получаем пользователя
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Генерируем токены
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Создаем ответ
            LoginResponse response = new LoginResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(jwtService.getExpirationTime());
            response.setUser(new LoginResponse.UserInfo(user));

            log.info("Успешный вход пользователя: {}", user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Ошибка при входе пользователя {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы", description = "Завершение сессии пользователя")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        log.info("Пользователь вышел из системы");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление токена", description = "Получение нового access token по refresh token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        try {
            // В реальной реализации нужно валидировать refresh token
            // и генерировать новый access token
            
            log.info("Обновление токена");
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Ошибка при обновлении токена: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
