package org.esimulate.frame.pojo;

import lombok.Getter;
import org.esimulate.frame.model.User;

@Getter
public class LoginDto {
    User user;
    String token;

    public LoginDto(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
