package com.example.hr_assistant.model.dto;

import com.example.hr_assistant.model.User;
import lombok.Data;

/**
 * Ответ на авторизацию
 */
@Data
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String email;
        private User.Role role;
        
        public UserInfo(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }
}
