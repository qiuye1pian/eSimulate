package org.esimulate.frame.pojo;

import lombok.Getter;
import org.esimulate.frame.model.User;

@Getter
public class LoginDto {

    private final String username;
    private final String role;
    private final String message;
    String token;

    private LoginDto(User user, String token, String message) {
        this.username = user.getUsername();
        this.role = user.getRole();
        this.token = token;
        this.message = message;
    }

    private LoginDto(String message) {
        this.username = "";
        this.role = "";
        this.token = "";
        this.message = message;
    }

    public static LoginDto success(User loggedInUser, String token) {
        return new LoginDto(loggedInUser, token, "登陆成功");
    }

    public static LoginDto fail() {
        return new LoginDto("用户名或密码不正确");
    }


}
